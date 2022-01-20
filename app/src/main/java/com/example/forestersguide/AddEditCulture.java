package com.example.forestersguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class AddEditCulture extends AppCompatActivity {

    private final int Pick_image = 1;

    DbHelper db;
    EditText edName=null, edDescription=null;
    ImageView img=null;
    Button btnLoad, btnDel, btnSave;
    String rowId, mode, name, description;
    Bitmap bmp=null;

    boolean imgOnlyLoad=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_culture);

        db = new DbHelper(this);

        edName = (EditText) findViewById(R.id.edName);
        edDescription = (EditText) findViewById(R.id.edDescription);
        img = (ImageView) findViewById(R.id.imageView);
        btnSave=(Button) findViewById(R.id.btnSave);
        btnLoad=(Button) findViewById(R.id.btnLoadImg);
        btnDel=(Button) findViewById(R.id.btnDelImg);

        img.setImageResource(R.drawable.ic_launcher_foreground);

        Intent intent = this.getIntent();
        this.mode = intent.getStringExtra("mode");

        if (!mode.equals("add")) {
            this.rowId = intent.getStringExtra("rowId");

            this.name = intent.getStringExtra("name");
            this.description = intent.getStringExtra("description");
            if (intent.getByteArrayExtra("bmp") != null) {
                this.bmp = BitmapFactory.decodeByteArray(intent.getByteArrayExtra("bmp"), 0,
                        intent.getByteArrayExtra("bmp").length);
                img.setImageBitmap(bmp);
                imgOnlyLoad=false;
            }

            edName.setText(name);
            edDescription.setText(description);
        }
    }

    //обработчик кнопки добавления
    public void onClickBtn(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
                String query = null;

                if (mode.equals("add"))
                    query = "insert into Культура(наименование,описание) values('" + edName.getText() + "','" +
                            edDescription.getText() + "');";
                else
                    query = "update Культура set наименование='" + edName.getText() + "', описание='" +
                            edDescription.getText() + "' where _id=" + rowId + ";";


                if (!db.sqlExec(query)) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Не возможно обновить данные в БД, повторите попытку!",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }

                if (mode.equals("add"))
                    rowId = db.getOneData("select MAX(_id) as _id from Культура", "_id", true);

                if (!bmp.equals(null)) {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 0, outputStream);

                    if (!db.updateBlob(rowId, "Культура", "изображение", outputStream.toByteArray())) {
                        Toast toast = Toast.makeText(getApplicationContext(),
                                "Не возможно обновить изображение в БД, повторите попытку!",
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                }

                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.btnLoadImg:
                //Вызываем стандартную галерею для выбора изображения с помощью Intent.ACTION_PICK
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                //Тип получаемых объектов - image:
                photoPickerIntent.setType("image/*");
                //Запускаем переход с ожиданием обратного результата в виде информации об изображении
                startActivityForResult(photoPickerIntent, Pick_image);
                break;

            case R.id.btnDelImg:
                if (!imgOnlyLoad)
                if (!db.sqlExec("update Культура set изображение=null where _id="+rowId)) {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Не возможно удалить изображение в БД, повторите попытку!",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    return;
                }
                bmp=null;
                img.setImageResource(R.drawable.ic_launcher_foreground);
                break;

            default:break;
        }
    }

    //Обрабатываем результат выбора в галерее
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (requestCode==Pick_image) {
                if (resultCode == RESULT_OK) {
                    try {
                        //Получаем URI изображения, преобразуем его в Bitmap
                        //объект и отображаем в элементе ImageView нашего интерфейса
                        final Uri imageUri = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        bmp = BitmapFactory.decodeStream(imageStream);
                        img.setImageBitmap(bmp);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        }
    }


}