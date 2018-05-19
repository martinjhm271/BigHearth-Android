package bigheart.escuelaing.eci.edu.bigheart.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
import com.hbb20.CountryCodePicker;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import bigheart.escuelaing.eci.edu.bigheart.R;
import bigheart.escuelaing.eci.edu.bigheart.model.Event;
import bigheart.escuelaing.eci.edu.bigheart.model.RolUser;
import bigheart.escuelaing.eci.edu.bigheart.model.Roles;
import bigheart.escuelaing.eci.edu.bigheart.model.Volunteer;
import bigheart.escuelaing.eci.edu.bigheart.network.service.NetworkException;
import bigheart.escuelaing.eci.edu.bigheart.network.service.RequestCallback;
import bigheart.escuelaing.eci.edu.bigheart.network.volunteer.NetworkVolunteerImpl;

public class RegistrationVolunteerActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputLayout t11,t12,t13,t16,t17,t18,t19,t20= null;
    EditText t14=null;
    ImageButton iv=null;
    final int REQUEST_CAMERA = 1;
    final int SELECT_FILE = 2;
    final CharSequence[] items = {"Take Photo", "Choose From Gallery"};
    Context applicationContext=this;
    private Uri selectedImageUri;
    private NetworkVolunteerImpl nvi;
    public String base64Photo="";
    CountryCodePicker ccp2=null;
    Spinner spinner2 = null;
    String selectSpinner2="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_volunteer);
        this.ccp2=findViewById(R.id.ccp2);
        this.spinner2=findViewById(R.id.spinner2);
        ArrayAdapter<String> genderAdapter = new  ArrayAdapter<String>(RegistrationVolunteerActivity.this,android.R.layout.simple_list_item_1,getResources().getStringArray(R.array.gender_list));
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(genderAdapter);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectSpinner2=getResources().getStringArray(R.array.gender_list)[i];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.iv=findViewById(R.id.imageButton2);
        this.t11=findViewById(R.id.t11);
        this.t12=findViewById(R.id.t12);

        this.t14=findViewById(R.id.t14);
        this.t16=findViewById(R.id.t16);
        this.t17=findViewById(R.id.t17);
        this.t18=findViewById(R.id.t18);
        this.t19=findViewById(R.id.t19);
        this.t20=findViewById(R.id.t20);
        nvi = new NetworkVolunteerImpl();



        final ImageButton button3 = findViewById(R.id.imageButton2);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                photo();
            }
        });

        final Button button4 = findViewById(R.id.button2);
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                save();
            }
        });

        grantPermissions();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); StrictMode.setVmPolicy(builder.build());
    }

    @NonNull
    public static Dialog createSingleChoiceAlertDialog(@NonNull Context context, @Nullable String title,
                                                       @Nullable CharSequence[] items,
                                                       @NonNull DialogInterface.OnClickListener optionSelectedListener,
                                                       @Nullable DialogInterface.OnClickListener cancelListener )
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems( items, optionSelectedListener );
        if ( cancelListener != null ) {
            builder.setNegativeButton("Cancel", cancelListener );
        }
        builder.setTitle( title );
        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case REQUEST_CAMERA:
                if(resultCode == -1){
                    iv.setImageURI(selectedImageUri);
                    break;
                }

                break;
            case SELECT_FILE:
                if(resultCode == -1){
                    try{
                        Uri imageUri = imageReturnedIntent.getData();
                        iv.setImageURI(imageUri);
                        break;
                    }catch(Exception e){}

                }
                break;
        }
    }


    public void save(){
        if(validation()){
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute( new Runnable() {
                @Override
                public void run() {
                    List<Event> evs=new ArrayList<>();
                    base64Photo=toBase64Photo();

                    Volunteer v = new Volunteer(0,
                            t11.getEditText().getText().toString(),
                            t12.getEditText().getText().toString(),
                            selectSpinner2,
                            new Date(t14.getText().toString()),
                            0,
                            ccp2.getSelectedCountryEnglishName(),
                            t16.getEditText().getText().toString(),
                            t17.getEditText().getText().toString(),
                            "",
                            0,
                            base64Photo,
                            new RolUser(t18.getEditText().getText().toString(),new Roles(2,"Volunteer")),
                            t19.getEditText().getText().toString(),
                            "",
                            evs);

                    nvi.createVolunteer(v,new RequestCallback<Volunteer>() {
                        @Override
                        public void onSuccess(final Volunteer response) {
                            Toast.makeText(applicationContext,"Registration success!!!!",Toast.LENGTH_SHORT).show();

                            //falta iniciar la actividad del login de carlos

                        }

                        @Override
                        public void onFailed(NetworkException e) {
                            Toast.makeText(applicationContext,"Error in the registration,please try again!!!!",Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            } );
        }
    }

    public void photo(){

        DialogInterface.OnClickListener optionSelectedListener =new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (items[which].equals("Take Photo")) {

                    int permission = ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA);
                    if(permission != PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(applicationContext,"You have to grant permissions, intent again!!",Toast.LENGTH_SHORT).show();
                        grantPermissions();
                    }
                    else{
                        int permission2 = ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE);
                        if(permission2 != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(applicationContext,"You have to grant permissions, intent again!!",Toast.LENGTH_SHORT).show();
                            grantPermissions();
                        }else{
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File photo = new File(Environment.getExternalStorageDirectory(),"Pic.jpg");
                            intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(photo));
                            selectedImageUri = Uri.fromFile(photo);
                            startActivityForResult(intent,REQUEST_CAMERA);
                        }
                    }


                } else if (items[which].equals("Choose From Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent,SELECT_FILE);
                }
            }
        };

        DialogInterface.OnClickListener cancelListener =new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

        Dialog d=createSingleChoiceAlertDialog(this,"Choose picture",items,optionSelectedListener,cancelListener);
        d.create();
        d.show();
    }




    public boolean validation(){

        if(t11.getEditText().getText().toString().length()==0 ||
                t12.getEditText().getText().toString().length()==0 ||
                t13.getEditText().getText().toString().length()==0 ||
                t14.getText().toString().length()==0 ||
                ccp2.getSelectedCountryEnglishName().length()==0 ||
                t16.getEditText().getText().toString().length()==0 ||
                t17.getEditText().getText().toString().length()==0 ||
                t18.getEditText().getText().toString().length()==0 ||
                t19.getEditText().getText().toString().length()==0 ||
                iv.getDrawable()==null || (!t19.getEditText().getText().toString().equals(t20.getEditText().getText().toString())) ){
            Toast.makeText(this,"Please complete all inputs and select an image",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void grantPermissions(){
        int permission = ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA);
        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
        }
        int permission2 = ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permission2 != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onClick(View v) {

    }

    public String toBase64Photo(){
        File imagefile = new File(selectedImageUri.getPath());
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(imagefile);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        Bitmap bm = BitmapFactory.decodeStream(fis);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }


}
