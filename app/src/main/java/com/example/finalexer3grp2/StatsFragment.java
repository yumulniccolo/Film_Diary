package com.example.finalexer3grp2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class StatsFragment extends Fragment {

    private TableLayout tableLayout;
    private Button btnDownloadCSV;
    private List<Film> filmList = new ArrayList<>();

    public StatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        tableLayout = view.findViewById(R.id.tableLayoutFilms);
        btnDownloadCSV = view.findViewById(R.id.btnDownloadCSV);

        btnDownloadCSV.setOnClickListener(v -> {
            if (!filmList.isEmpty()) {
                exportToCSV(filmList);
            } else {
                Toast.makeText(getContext(), "No data to export", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FilmDatabase.getDatabase(requireContext()).filmDao().getAllFilms().observe(getViewLifecycleOwner(), films -> {
            filmList = films;
            populateTable(films);
        });
    }

    private void populateTable(List<Film> films) {
        int childCount = tableLayout.getChildCount();
        if (childCount > 1) {
            tableLayout.removeViews(1, childCount - 1);
        }

        for (Film film : films) {
            TableRow row = new TableRow(getContext());
            row.setPadding(0, 4, 0, 4);

            row.addView(createTextView(String.valueOf(film.getId()), 0.5f));
            row.addView(createTextView(film.getTitle(), 2.0f));
            row.addView(createTextView(film.getDirector(), 3.0f));
            row.addView(createTextView(film.getYear(), 1.0f));
            row.addView(createTextView(film.getGenres(), 1.5f));

            tableLayout.addView(row);
        }
    }

    private TextView createTextView(String text, float weight) {
        TextView textView = new TextView(getContext());
        textView.setText(text != null ? text : "");
        textView.setPadding(12, 12, 12, 12);
        textView.setTextAppearance(com.google.android.material.R.style.TextAppearance_Material3_BodyMedium);
        textView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, weight));
        textView.setSingleLine(false);
        return textView;
    }

    private void exportToCSV(List<Film> films) {
        String fileName = "Films_Report_" + System.currentTimeMillis() + ".csv";
        ContentResolver resolver = requireContext().getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
        
        Uri collection;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI;
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/");
        } else {
            collection = MediaStore.Files.getContentUri("external");
        }

        Uri uri = resolver.insert(collection, contentValues);

        if (uri != null) {
            try (OutputStream outputStream = resolver.openOutputStream(uri);
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream))) {

                writer.write("ID,Title,Director,Year,Genre");
                writer.newLine();

                for (Film film : films) {
                    writer.write(film.getId() + "," +
                            escapeCsv(film.getTitle()) + "," +
                            escapeCsv(film.getDirector()) + "," +
                            escapeCsv(film.getYear()) + "," +
                            escapeCsv(film.getGenres()));
                    writer.newLine();
                }

                writer.flush();
                Toast.makeText(getContext(), "File Downloaded: " + fileName, Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                Toast.makeText(getContext(), "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Failed to create file", Toast.LENGTH_SHORT).show();
        }
    }

    private String escapeCsv(String data) {
        if (data == null) return "";
        if (data.contains(",") || data.contains("\"") || data.contains("\n")) {
            return "\"" + data.replace("\"", "\"\"") + "\"";
        }
        return data;
    }
}
