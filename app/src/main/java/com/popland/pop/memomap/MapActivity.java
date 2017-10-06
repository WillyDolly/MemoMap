package com.popland.pop.memomap;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qozix.tileview.TileView;
import com.qozix.tileview.markers.MarkerLayout;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {
TileView tileView;
final int GALLERY_CODE = 111;
final int CAMERA_CODE = 222;
ImageView ivPic;
int index2;
Dialog dialog;
    ArrayList<Info> infoArr = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cursor cursor = SplashActivity.dataBase.getData("SELECT * FROM MaSy");
        while(cursor.moveToNext())
            infoArr.add(new Info(cursor.getString(1) , cursor.getString(2) , cursor.getBlob(3)));
        Toast.makeText(this,infoArr.size()+"",Toast.LENGTH_SHORT).show();

        //create Map
        tileView  = new TileView(this);
        tileView.setSize(480,3520);
        tileView.addDetailLevel(1f,"land/1000/%d_%d.png");
        tileView.addDetailLevel(0.5f,"land/500/%d_%d.png");
        tileView.addDetailLevel(0.25f,"land/250/%d_%d.png");
        tileView.addDetailLevel(0.125f,"land/125/%d_%d.png");
        tileView.setScaleLimits(0,5);
        tileView.setScale(0);
        tileView.defineBounds(0,0,29,219);
        tileView.setShouldRenderWhilePanning(true);
        setContentView(tileView);

        //add Markers
        int index1 = 0;
        for(double[] position: positions) {
            TextView number = new TextView(this);
            number.setText(index1+"");
            number.setTextSize(27);
            number.setTextColor(Color.parseColor("#ccff00"));
            number.setTag(index1);
            tileView.addMarker(number, position[0], position[1], -0.5f, -1f);
            index1++;
        }
        tileView.setMarkerTapListener(mMarkerTapListener);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        tileView.scrollToAndCenter(positions.get(0)[0],positions.get(0)[1]);
    }

    public MarkerLayout.MarkerTapListener mMarkerTapListener = new MarkerLayout.MarkerTapListener() {
        @Override
        public void onMarkerTap(View view, int x, int y) {
            index2 = (int) view.getTag();
            dialog = new Dialog(MapActivity.this);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.info_dialog);
            dialog.setCanceledOnTouchOutside(true);

            final EditText edtWord = (EditText) dialog.findViewById(R.id.edtWord);
            ivPic = (ImageView) dialog.findViewById(R.id.ivPic);
            ImageButton ibPhoto = (ImageButton) dialog.findViewById(R.id.ibPhoto);
            ImageButton ibGallery = (ImageButton) dialog.findViewById(R.id.ibGallery);
            ImageButton ibSave = (ImageButton) dialog.findViewById(R.id.ibSave);

            edtWord.setText(infoArr.get(index2).word);
            byte[] anh = infoArr.get(index2).image;
            if(anh!=null)
                ivPic.setImageBitmap(BitmapFactory.decodeByteArray(anh,0,anh.length));

            ibPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(in, CAMERA_CODE);
                }
            });

            ibGallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY_CODE);
                }
            });

            ibSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //resize image before save in database
                    String newWord = edtWord.getText().toString();
                    SplashActivity.dataBase.Update_Data(infoArr.get(index2).so, newWord, ImageView_To_ByteArray(ivPic));
                    infoArr.set(index2, new Info(infoArr.get(index2).so, newWord, ImageView_To_ByteArray(ivPic)));
                    dialog.dismiss();
                    Toast.makeText(MapActivity.this, "update ok", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();
        }
    };


    byte[] ImageView_To_ByteArray(ImageView iv){
        BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] mang = baos.toByteArray();
        return mang;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK && null!=data) {
            switch (requestCode){
                case CAMERA_CODE:
                    Bitmap bm = (Bitmap) data.getExtras().get("data");
                    ivPic.setImageBitmap(bm);
                    break;
                case GALLERY_CODE:
                        Uri uri = data.getData();
                        Picasso.with(MapActivity.this).load(uri).fit().into(ivPic);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.arrow,menu);
        menu.getItem(0).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(MapActivity.this,CheckActivity.class);
                startActivity(i);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {

    }

    ArrayList<double[]> positions = new ArrayList<>();
    {positions.add(new double[]{1,217});
        positions.add(new double[]{16,217});
        positions.add(new double[]{25,217});
        positions.add(new double[]{28,214});
        positions.add(new double[]{27,204});
        positions.add(new double[]{21,205});
        positions.add(new double[]{16,205});
        positions.add(new double[]{12,205});
        positions.add(new double[]{10,208});
        positions.add(new double[]{4,206});

        positions.add(new double[]{2,194});
        positions.add(new double[]{9,192});
        positions.add(new double[]{16,194});
        positions.add(new double[]{21,196});
        positions.add(new double[]{25,196});
        positions.add(new double[]{27,186});
        positions.add(new double[]{25,180});
        positions.add(new double[]{18,182});
        positions.add(new double[]{11,184});
        positions.add(new double[]{4,180});

        positions.add(new double[]{2,171});
        positions.add(new double[]{7,172});
        positions.add(new double[]{12,174});
        positions.add(new double[]{21,171});
        positions.add(new double[]{28,172});
        positions.add(new double[]{26,169});
        positions.add(new double[]{27,161});
        positions.add(new double[]{20,163});
        positions.add(new double[]{12,160});
        positions.add(new double[]{2,164});

        positions.add(new double[]{1,154});
        positions.add(new double[]{5,153});
        positions.add(new double[]{9,155});
        positions.add(new double[]{16,150});
        positions.add(new double[]{23,150});
        positions.add(new double[]{27,152});
        positions.add(new double[]{27,141});
        positions.add(new double[]{16,138});
        positions.add(new double[]{9,145});
        positions.add(new double[]{5,140});

        positions.add(new double[]{3,131});
        positions.add(new double[]{9,129});
        positions.add(new double[]{15,135});
        positions.add(new double[]{21,129});
        positions.add(new double[]{26,124});
        positions.add(new double[]{24,127});
        positions.add(new double[]{15,115});
        positions.add(new double[]{15,125});
        positions.add(new double[]{5,120});
        positions.add(new double[]{5,125});

        positions.add(new double[]{3,116});
        positions.add(new double[]{8,109});
        positions.add(new double[]{14,111});
        positions.add(new double[]{21,109});
        positions.add(new double[]{27,116});
        positions.add(new double[]{25,104});
        positions.add(new double[]{20,104});
        positions.add(new double[]{15,104});
        positions.add(new double[]{10,104});
        positions.add(new double[]{5,104});

        positions.add(new double[]{1,86});
        positions.add(new double[]{12,86});
        positions.add(new double[]{16,92});
        positions.add(new double[]{19,93});
        positions.add(new double[]{28,91});
        positions.add(new double[]{27,86});
        positions.add(new double[]{27,82});
        positions.add(new double[]{15,80});
        positions.add(new double[]{16,85});
        positions.add(new double[]{8,83});

        positions.add(new double[]{2,78});
        positions.add(new double[]{6,77});
        positions.add(new double[]{9,77});
        positions.add(new double[]{15,77});
        positions.add(new double[]{18,73});
        positions.add(new double[]{24,70});
        positions.add(new double[]{20,71});
        positions.add(new double[]{15,72});
        positions.add(new double[]{11,72});
        positions.add(new double[]{5,70});

        positions.add(new double[]{3,55});
        positions.add(new double[]{11,55});
        positions.add(new double[]{19,55});
        positions.add(new double[]{24,54});
        positions.add(new double[]{27,54});
        positions.add(new double[]{24,52});
        positions.add(new double[]{27,51});
        positions.add(new double[]{26,44});
        positions.add(new double[]{17,44});
        positions.add(new double[]{7,44});

        positions.add(new double[]{2,38});
        positions.add(new double[]{7,32});
        positions.add(new double[]{10,32});
        positions.add(new double[]{13,34});
        positions.add(new double[]{19,36});
        positions.add(new double[]{26,26});
        positions.add(new double[]{20,26});
        positions.add(new double[]{18,27});
        positions.add(new double[]{14,28});
        positions.add(new double[]{2,23});

        positions.add(new double[]{2,15});
        positions.add(new double[]{8,11});
        positions.add(new double[]{13,14});
        positions.add(new double[]{14,9});
        positions.add(new double[]{22,11});
        positions.add(new double[]{24,6});
        positions.add(new double[]{26,2});
        positions.add(new double[]{17,4});
        positions.add(new double[]{11,5});
        positions.add(new double[]{5,2});}
}
