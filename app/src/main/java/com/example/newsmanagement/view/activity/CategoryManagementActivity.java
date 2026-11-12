package com.example.newsmanagement.view.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsmanagement.R;
import com.example.newsmanagement.view.adapter.CategoryAdapter;
import com.example.newsmanagement.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryManagementActivity extends AppCompatActivity {

    private ImageView btnBack;
    private View btnAddNewCategory;
    private EditText etSearchCategory;
    private RecyclerView rvCategories;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category_management);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupData();
        setupEvents();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnAddNewCategory = findViewById(R.id.btnAddNewCategory);
        etSearchCategory = findViewById(R.id.etSearchCategory);
        rvCategories = findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupData() {
        categoryList = new ArrayList<>();
        categoryList.add(new Category("TL01", "Bóng đá", "Tin tức giải đấu, bóng đá trong nước và quốc tế", 320));
        categoryList.add(new Category("TL02", "Xã hội", "Sự kiện đời sống, thời tiết, giao thông đô thị", 450));
        categoryList.add(new Category("TL03", "Pháp luật", "An ninh trật tự, xét xử, tư vấn pháp lý", 210));
        categoryList.add(new Category("TL04", "Ô tô - Xe máy", "Thị trường xe hơi, xe máy điện, đánh giá chuyên sâu", 160));
        categoryList.add(new Category("TL05", "Thế giới", "Tin tức thời sự quốc tế nóng hổi", 100));

        categoryAdapter = new CategoryAdapter(this, categoryList, new CategoryAdapter.OnCategoryActionListener() {
            @Override
            public void onEdit(Category category) {
                showEditCategoryDialog(category);
            }

            @Override
            public void onDelete(Category category) {
                showDeleteConfirmDialog(category);
            }
        });
        rvCategories.setAdapter(categoryAdapter);
    }

    private void setupEvents() {
        btnBack.setOnClickListener(v -> finish());
        btnAddNewCategory.setOnClickListener(v -> showAddCategoryDialog());

        // Lắng nghe tìm kiếm trực tiếp
        etSearchCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                categoryAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void showAddCategoryDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null));

        EditText etCode = dialog.findViewById(R.id.etDialogCategoryCode);
        EditText etName = dialog.findViewById(R.id.etDialogCategoryName);
        EditText etDesc = dialog.findViewById(R.id.etDialogCategoryDesc);
        View btnCancel = dialog.findViewById(R.id.btnDialogCancel);
        View btnSave = dialog.findViewById(R.id.btnDialogSave);

        etCode.setText(String.format("TL%02d", categoryList.size() + 1));
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String code = etCode.getText().toString().trim();
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            if (code.isEmpty()) {
                etCode.setError("Vui lòng nhập mã loại!");
                etCode.requestFocus();
                return;
            }
            if (name.isEmpty()) {
                etName.setError("Vui lòng nhập tên thể loại!");
                etName.requestFocus();
                return;
            }

            for (Category cat : categoryList) {
                if (cat.getId().equalsIgnoreCase(code)) {
                    etCode.setError("Mã loại này đã tồn tại!");
                    etCode.requestFocus();
                    return;
                }
            }

            categoryList.add(0, new Category(code, name, desc, 0));
            categoryAdapter.updateData(categoryList);
            rvCategories.scrollToPosition(0);
            Toast.makeText(this, "Đã thêm thể loại mới!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        formatDialogWindow(dialog);
        dialog.show();
    }

    private void showEditCategoryDialog(Category category) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(LayoutInflater.from(this).inflate(R.layout.dialog_edit_category, null));

        EditText etCode = dialog.findViewById(R.id.etEditCategoryCode);
        EditText etName = dialog.findViewById(R.id.etEditCategoryName);
        EditText etDesc = dialog.findViewById(R.id.etEditCategoryDesc);
        View btnCancel = dialog.findViewById(R.id.btnEditDialogCancel);
        View btnSave = dialog.findViewById(R.id.btnEditDialogSave);

        etCode.setText(category.getId());
        etName.setText(category.getName());
        etDesc.setText(category.getDescription());

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            String newDesc = etDesc.getText().toString().trim();

            if (newName.isEmpty()) {
                etName.setError("Vui lòng nhập tên thể loại!");
                etName.requestFocus();
                return;
            }

            category.setName(newName);
            category.setDescription(newDesc);
            categoryAdapter.updateData(categoryList);
            Toast.makeText(this, "Đã cập nhật thể loại: " + category.getId(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        formatDialogWindow(dialog);
        dialog.show();
    }

    private void showDeleteConfirmDialog(Category category) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(LayoutInflater.from(this).inflate(R.layout.dialog_delete_confirm, null));

        TextView tvDeleteMessage = dialog.findViewById(R.id.tvDeleteMessage);
        View btnCancel = dialog.findViewById(R.id.btnCancelDelete);
        View btnConfirm = dialog.findViewById(R.id.btnConfirmDelete);

        tvDeleteMessage.setText("Bạn có chắc chắn muốn xóa thể loại \"" + category.getName() + "\" (" + category.getId() + ") không?");
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            categoryList.remove(category);
            categoryAdapter.updateData(categoryList);
            Toast.makeText(this, "Đã xóa thể loại: " + category.getName(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        formatDialogWindow(dialog);
        dialog.show();
    }

    private void formatDialogWindow(Dialog dialog) {
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.92);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
