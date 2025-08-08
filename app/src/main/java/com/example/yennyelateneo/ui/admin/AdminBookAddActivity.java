package com.example.yennyelateneo.ui.admin;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.yennyelateneo.R;
import com.example.yennyelateneo.data.model.Book;
import com.example.yennyelateneo.domain.controller.BookController;
import com.example.yennyelateneo.domain.interfaces.OnBookAddResult;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AdminBookAddActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> imagePickerLauncherAdd;
    private ActivityResultLauncher<String> permissionLauncherAdd;

    private EditText txtTitleAdd, txtAuthorAdd, txtPriceAdd, txtDescriptionAdd;
    private Button btnAdd;
    private ImageButton backAdd;
    private ImageView imagedownloadAdd;
    private Uri imageUriAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_book_add);


        txtTitleAdd = findViewById(R.id.addTextTitle);
        txtAuthorAdd = findViewById(R.id.addTextAuthor);
        txtPriceAdd = findViewById(R.id.addTextPrice);
        txtDescriptionAdd = findViewById(R.id.addTextDescription);
        imagedownloadAdd = findViewById(R.id.addImagePreview);
        btnAdd = findViewById(R.id.btnSubmitAddBook);
        backAdd = findViewById(R.id.btnBackAddBookAdmin);

        backAdd.setOnClickListener(v -> finish());

        permissionLauncherAdd = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        launchImagePicker();
                    } else {
                        Toast.makeText(this, "Permiso denegado para acceder a las imágenes", Toast.LENGTH_SHORT).show();
                    }
                }
        );


        imagePickerLauncherAdd = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUriAdd = result.getData().getData();
                        imagedownloadAdd.setImageURI(imageUriAdd);
                    }
                }
        );

        imagedownloadAdd.setOnClickListener(v -> openGallery());

        btnAdd.setOnClickListener(v -> {addBook();});
    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            launchImagePicker();
        } else {
            permissionLauncherAdd.launch(Manifest.permission.READ_MEDIA_IMAGES);
        }
    }

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncherAdd.launch(intent);
    }

    private void addBook() {
        String title = txtTitleAdd.getText().toString();
        String author = txtAuthorAdd.getText().toString();
        String description = txtDescriptionAdd.getText().toString();
        String priceText = txtPriceAdd.getText().toString();

        BookValidationResult result = validateAndLoadImage(title, author, description, priceText);
        if (result == null) return;

        Book book = new Book(title, author, description, result.price, result.fileName);

        BookController.addBookAsync(book, result.imageBytes, result.fileName, new OnBookAddResult() {
            @Override
            public void onSuccess(boolean success) {
                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(AdminBookAddActivity.this, "Libro agregado correctamente", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(AdminBookAddActivity.this, "No se pudo agregar el libro", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> Toast.makeText(AdminBookAddActivity.this, "Error inesperado al agregar el libro", Toast.LENGTH_SHORT).show());
            }
        });
    }


    private String getFileExtension(Uri uri) {
        String extension = null;

        String mimeType = getContentResolver().getType(uri);
        if (mimeType != null) {
            extension = android.webkit.MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        }

        if (extension == null || extension.isEmpty()) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1 && cursor.moveToFirst()) {
                    String fileName = cursor.getString(nameIndex);
                    int dotIndex = fileName.lastIndexOf('.');
                    if (dotIndex != -1) {
                        extension = fileName.substring(dotIndex + 1);
                    }
                }
                cursor.close();
            }
        }

        if (extension == null) extension = "";

        return extension.toLowerCase();
    }

    private BookValidationResult validateAndLoadImage(String title, String author, String description, String priceText) {
        if (imageUriAdd == null || title.trim().isEmpty() || author.trim().isEmpty() || description.trim().isEmpty() || priceText.trim().isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return null;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price <= 0) {
                Toast.makeText(this, "El precio debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Precio inválido. Debe ser un número", Toast.LENGTH_SHORT).show();
            return null;
        }

        String extension = getFileExtension(imageUriAdd);

        Set<String> allowedExtensions = new HashSet<>(Arrays.asList("jpg", "jpeg", "webp"));
        if (!allowedExtensions.contains(extension)) {
            Toast.makeText(this, "Formato no permitido. Solo JPG, JPEG o WEBP", Toast.LENGTH_SHORT).show();
            return null;
        }

        String fileName = UUID.randomUUID().toString() + "." + extension;

        try (InputStream inputStream = getContentResolver().openInputStream(imageUriAdd)) {
            if (inputStream == null) {
                Toast.makeText(this, "Error al leer la imagen seleccionada", Toast.LENGTH_SHORT).show();
                return null;
            }
            byte[] imageBytes = IOUtils.toByteArray(inputStream);

            return new BookValidationResult(fileName, imageBytes, price);

        } catch (IOException e) {
            Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
            return null;
        }
    }




}