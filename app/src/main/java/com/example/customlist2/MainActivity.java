package com.example.customlist2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Random random = new Random();
    private ItemAdapter itemAdapter;
    private List<Drawable> images = new ArrayList<>();
    private ListView listView;
    private File logFile ;
    private File file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        listView = findViewById(R.id.listViewMain);
        logFile = new File (getApplicationContext().getExternalFilesDir(null), "homework.txt");

        dropImages();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateRandomItemData();
            }
        });

        itemAdapter = new ItemAdapter(this, null);
        listView.setAdapter(itemAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showInfo(position);
                return true;
            }
        });
    }

    private void dropImages() {
        images.add(getDrawable(R.drawable.ic_favorite_border_black_24dp));
        images.add(getDrawable(R.drawable.ic_insert_emoticon_black_24dp));
        images.add(getDrawable(R.drawable.ic_star_border_black_24dp));
    }

    private void generateRandomItemData() {
        ItemData itemData = new ItemData(
                images.get(random.nextInt(images.size())),
                "Hello" + itemAdapter.getCount(),
                "It\'s me"
        );
        itemAdapter.addItem(itemData);

        FileWriter writer = null;
        file = new File(itemData.toString());
        {
            try {
                writer = new FileWriter(logFile, true);
                writer.append(file + ";");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void showInfo(int position) {
        ItemData itemData = (ItemData) itemAdapter.getItem(position);
        Toast.makeText(MainActivity.this,
                "Title: " + itemData.getTitle() + "\n" +
                        "Subtitle: " + itemData.getSubtitle() ,
                Toast.LENGTH_SHORT).show();
    }

    public void delete(View v){
        int position = listView.getPositionForView(v);
        itemAdapter.removeItem(position);
        File files = new File(v.toString() + ";");
        files.delete();
    }

}
