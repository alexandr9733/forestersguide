package com.example.forestersguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    DbHelper db;
    ListView lv;
    Button btnAdd;
    EditText edFind;
    ListCultureAdapter lma;
    String selectedID=null;
    int selectedPosition=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //ищем наши компоненты - списки вывода
        lv = (ListView) findViewById(R.id.lvMain);
        edFind=(EditText) findViewById(R.id.edFind);
        btnAdd=(Button)  findViewById(R.id.btnAdd);

        //создаем обьект нашего класса для работы с бд
        db = new DbHelper(this);

        lv.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter_view, View v, int i, long l) {
                selectedPosition=i;
                //получаем необходимые данные из курсора
                selectedID = String.valueOf(lma.getItemId(i));
                lv.showContextMenu();
            }
        });

        edFind.addTextChangedListener ( new TextWatcher() {

            public void afterTextChanged ( Editable s ) {
                //что-то делаем после изменения
            }

            public void beforeTextChanged ( CharSequence s, int start, int count, int after ) {
                //что-то делаем до изменения
            }

            //пока текст меняется - загружаем данные
            public void onTextChanged ( CharSequence s, int start, int before, int count ) {
                loadData(s.toString());
            }
        });

        // добавляем контекстное меню к списку
        registerForContextMenu(lv);
    }

    private void loadData(String strLike)
    {
        try {
        //Init adapter
        lma  = new ListCultureAdapter(this,db.getListMashrooms(strLike));
        //Set adapter for listview
        lv.setAdapter(lma);

//        if(lma.getCount()==0)
//            btnAdd.setVisibility(View.VISIBLE);
//        else
//            btnAdd.setVisibility(View.INVISIBLE);
        }
        catch(Exception e)
        {
            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.BOTTOM, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setText(e.getMessage());
            toast.show();

        }
    }

    //перегруженный метод для создания выпадающего меню
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(Menu.NONE, 1, Menu.NONE, "Добавить");
        menu.add(Menu.NONE, 2, Menu.NONE, "Редактировать");
        menu.add(Menu.NONE, 3, Menu.NONE, "Удалить");
    }

    //обработчик событий выбора пунктов меню
    public boolean onContextItemSelected(MenuItem item) {
        // получаем из пункта контекстного меню данные по пункту списка
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId())
        {
            case 1:
                this.onClickBtn(btnAdd);
                break;

            case 2:
                Intent intentAddEdit = new Intent(MainActivity.this, AddEditCulture.class);
                intentAddEdit.putExtra("mode","edit");
                intentAddEdit.putExtra("rowId", selectedID);

                intentAddEdit.putExtra("name",  lma.getItem(selectedPosition).getName());
                intentAddEdit.putExtra("description", lma.getItem(selectedPosition).getDescription());
                intentAddEdit.putExtra("bmp", lma.getItem(selectedPosition).getImageByteArray());
                startActivity(intentAddEdit);
                break;

            case 3:
                if (!db.sqlExec("delete from Культура where _id="+selectedID)) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Не возможно удалить изображение в БД, повторите попытку!",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }

                loadData(edFind.getText().toString());
                break;

            default:break;
        }

        return super.onContextItemSelected(item);
    }

    //перегруженный метод активности - когда активность возобновляет работу
    @Override
    public void onResume(){
        super.onResume();
        loadData(edFind.getText().toString());
    }

    //обработчик кнопки добавления
    public void onClickBtn(View v) {
        if (v.getId() == R.id.btnAdd)
        {
            Intent intentAddEdit = new Intent(MainActivity.this, AddEditCulture.class);
            intentAddEdit.putExtra("mode","add");
            startActivity(intentAddEdit);
        }
    }
}