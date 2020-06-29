package com.example.customlist2;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
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
    private File logFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        listView = findViewById(R.id.listViewMain);
        logFile = new File(getApplicationContext().getExternalFilesDir(null), "idkwid.txt");

        dropImages();

        itemAdapter = new ItemAdapter(this, null);

        readItems();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateRandomItemData();
            }
        });

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
                "It's me"
        );
        itemAdapter.addItem(itemData);

        try (FileWriter writer = new FileWriter(logFile, true)) {
            saveItem(writer, itemData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveItem(FileWriter writer, ItemData itemData) throws IOException {
        writer.append(itemData.getTitle());
        writer.append(";");
        writer.append(itemData.getSubtitle());
        writer.append(";");
        writer.append(String.valueOf(images.indexOf(itemData.getImage())));
        writer.append(";");
    }

    private void readItems() {
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String[] split = reader.readLine().split(";");
            for (int i = 0 ; i < split.length; i += 3) { // почему i+=3 ???
                String title = split[i];
                String subtitle = split[i + 1];
                Drawable image = images.get(Integer.parseInt(split[i + 2]));
                itemAdapter.addItem(new ItemData(image, title, subtitle));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showInfo(int position) {
        ItemData itemData = (ItemData) itemAdapter.getItem(position);
        Toast.makeText(MainActivity.this,
                "Title: " + itemData.getTitle() + "\n" +
                        "Subtitle: " + itemData.getSubtitle(),
                Toast.LENGTH_SHORT).show();
    }

    public void delete(View v) {
        int position = listView.getPositionForView(v);
        itemAdapter.removeItem(position);

        logFile.delete();

        try (FileWriter writer = new FileWriter(logFile, true)) {
            for (int i = 0; i < itemAdapter.getCount(); i++) {
                saveItem(writer, (ItemData) itemAdapter.getItem(i));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
