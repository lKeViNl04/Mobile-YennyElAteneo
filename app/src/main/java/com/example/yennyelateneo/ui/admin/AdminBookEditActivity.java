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

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Toast;
import androidx.core.content.ContextCompat;

import com.example.yennyelateneo.R;
import com.example.yennyelateneo.data.model.Book;
import com.example.yennyelateneo.domain.controller.BookController;
import com.example.yennyelateneo.domain.interfaces.OnBookEditResult;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class AdminBookEditActivity extends AppCompatActivity {
    private ActivityResultLauncher<Intent> imagePickerLauncherEdit;
    private ActivityResultLauncher<String> permissionLauncherEdit;

    private String originalTitle, originalAuthor, originalDescription, originalImage;
    private double originalPrice;
    private EditText txtTitleEdit, txtAuthorEdit, txtPriceEdit, txtDescriptionEdit;
    private Button btnEdit;
    private ImageButton backEdit;
    private ImageView imagedownloadEdit;
    private Uri imageUriEdit;
    private NumberFormat format;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_book_edit);

        format = NumberFormat.getCurrencyInstance(new Locale("es", "AR"));

        txtTitleEdit = findViewById(R.id.editTextTitle);
        txtAuthorEdit = findViewById(R.id.editTextAuthor);
        txtPriceEdit = findViewById(R.id.editTextPrice);
        txtDescriptionEdit = findViewById(R.id.editTextDescription);
        imagedownloadEdit = findViewById(R.id.editImagePreview);
        btnEdit = findViewById(R.id.btnSubmitEditBook);
        backEdit = findViewById(R.id.btnBackEditBookAdmin);

        backEdit.setOnClickListener(v -> finish());

        Bundle bookBundle = getIntent().getExtras();

        if (bookBundle != null) {
            Long id = bookBundle.getLong("id", -1);
            String title = bookBundle.getString("title", "Not title");
            String author = bookBundle.getString("author", "Not author");
            String description = bookBundle.getString("description", "Not description");
            String image = bookBundle.getString("image", "Not image");
            Double price = bookBundle.getDouble("price", 0.0);

            originalTitle = title;
            originalAuthor = author;
            originalDescription = description;
            originalImage = image;
            originalPrice = price;

            txtTitleEdit.setText(title);
            txtAuthorEdit.setText(author);
            txtDescriptionEdit.setText(description);
            txtPriceEdit.setText(format.format(price));

            if (image != null && !image.isEmpty()) {
                Picasso.get()
                        .load(image)
                        .placeholder(R.drawable.loadinbook)
                        .error(R.drawable.errorimage)
                        .into(imagedownloadEdit);
            }

            permissionLauncherEdit = registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            launchImagePicker();
                        } else {
                            Toast.makeText(this, "Permiso denegado para acceder a las imágenes", Toast.LENGTH_SHORT).show();
                        }
                    }
            );


            imagePickerLauncherEdit = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            imageUriEdit = result.getData().getData();
                            imagedownloadEdit.setImageURI(imageUriEdit);
                        }
                    }
            );

            imagedownloadEdit.setOnClickListener(v -> openGallery());

            btnEdit.setOnClickListener(v -> EditBook(id));
        } else {
        }

    }

    private void openGallery() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            launchImagePicker();
        } else {
            permissionLauncherEdit.launch(Manifest.permission.READ_MEDIA_IMAGES);
        }
    }

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        imagePickerLauncherEdit.launch(intent);
    }

    private void EditBook(Long id) {
        String title = txtTitleEdit.getText().toString();
        String author = txtAuthorEdit.getText().toString();
        String description = txtDescriptionEdit.getText().toString();
        String priceText = txtPriceEdit.getText().toString();

        BookValidationResult result = validateAndLoadImage(title, author, description, priceText);
        if (result == null) return;

        String imageFinal = result.fileName != null ? result.fileName : originalImage;
        Book book = new Book(id, title, author, description, result.price, imageFinal);
        BookController.editBookAsync(book, result.imageBytes, result.fileName, new OnBookEditResult() {
            @Override
            public void onSuccess(boolean success) {
                runOnUiThread(() -> {
                    if (success) {
                        Toast.makeText(AdminBookEditActivity.this, "Libro Modificado correctamente", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("updated", true);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(AdminBookEditActivity.this, "No se pudo Modificar el libro", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(() -> Toast.makeText(AdminBookEditActivity.this, "Error inesperado al Modificar el libro", Toast.LENGTH_SHORT).show());
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
        String currentTitle = title.trim();
        String currentAuthor = author.trim();
        String currentDescription = description.trim();

        String cleanPrice = priceText.replaceAll("[^\\d.,]", "");
        cleanPrice = cleanPrice.replace(".", "");
        String plainPriceText = cleanPrice.replace(",", ".");

        double price;
        try {
            price = Double.parseDouble(plainPriceText);
            if (price <= 0) {
                Toast.makeText(this, "El precio debe ser mayor a 0", Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Precio inválido. Debe ser un número", Toast.LENGTH_SHORT).show();
            return null;
        }

        boolean titleChanged = !currentTitle.equals(originalTitle);
        boolean authorChanged = !currentAuthor.equals(originalAuthor);
        boolean descriptionChanged = !currentDescription.equals(originalDescription);
        boolean priceChanged = price != originalPrice;
        boolean imageChanged = imageUriEdit != null && (originalImage == null || !imageUriEdit.toString().equals(originalImage));

        if (!titleChanged && !authorChanged && !descriptionChanged && !priceChanged && !imageChanged) {
            Toast.makeText(this, "Por favor modifique algo", Toast.LENGTH_SHORT).show();
            return null;
        }

        if (imageChanged) {
            String extension = getFileExtension(imageUriEdit);
            Set<String> allowedExtensions = new HashSet<>(Arrays.asList("jpg", "jpeg", "webp"));

            if (!allowedExtensions.contains(extension)) {
                Toast.makeText(this, "Formato no permitido. Solo JPG, JPEG o WEBP", Toast.LENGTH_SHORT).show();
                return null;
            }

            String fileName = UUID.randomUUID().toString() + "." + extension;

            try (InputStream inputStream = getContentResolver().openInputStream(imageUriEdit)) {
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
        } else {
            return new BookValidationResult(null, null, price);
        }
    }


}