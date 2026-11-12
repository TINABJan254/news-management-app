package com.example.newsmanagement;

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

import com.example.newsmanagement.adapter.CategoryAdapter;
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
        setupSampleData();
        setupEvents();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnAddNewCategory = findViewById(R.id.btnAddNewCategory);
        etSearchCategory = findViewById(R.id.etSearchCategory);
        rvCategories = findViewById(R.id.rvCategories);

        rvCategories.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupSampleData() {
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
        // Nút Back quay lại màn hình trước
        btnBack.setOnClickListener(v -> finish());

        // Nút Thêm mới: Mở popup thêm thể loại
        btnAddNewCategory.setOnClickListener(v -> showAddCategoryDialog());

        // Tìm kiếm theo thời gian thực (TextWatcher)
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

    /**
     * Popup Thêm mới Thể loại (dùng dialog_add_category.xml)
     */
    private void showAddCategoryDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_category, null);
        dialog.setContentView(dialogView);

        EditText etCategoryCode = dialogView.findViewById(R.id.etDialogCategoryCode);
        EditText etCategoryName = dialogView.findViewById(R.id.etDialogCategoryName);
        EditText etCategoryDesc = dialogView.findViewById(R.id.etDialogCategoryDesc);
        View btnDialogCancel = dialogView.findViewById(R.id.btnDialogCancel);
        View btnDialogSave = dialogView.findViewById(R.id.btnDialogSave);

        // Gợi ý mã thể loại tự động ví dụ TL06
        etCategoryCode.setText(String.format("TL%02d", categoryList.size() + 1));

        btnDialogCancel.setOnClickListener(v -> dialog.dismiss());

        btnDialogSave.setOnClickListener(v -> {
            String code = etCategoryCode.getText().toString().trim();
            String name = etCategoryName.getText().toString().trim();
            String desc = etCategoryDesc.getText().toString().trim();

            if (code.isEmpty()) {
                etCategoryCode.setError("Vui lòng nhập mã loại!");
                etCategoryCode.requestFocus();
                return;
            }

            if (name.isEmpty()) {
                etCategoryName.setError("Vui lòng nhập tên thể loại!");
                etCategoryName.requestFocus();
                return;
            }

            for (Category cat : categoryList) {
                if (cat.getId().equalsIgnoreCase(code)) {
                    etCategoryCode.setError("Mã loại này đã tồn tại!");
                    etCategoryCode.requestFocus();
                    return;
                }
            }

            Category newCategory = new Category(code, name, desc, 0);
            categoryList.add(0, newCategory);
            categoryAdapter.updateData(categoryList);
            rvCategories.scrollToPosition(0);
            Toast.makeText(this, "Đã thêm thể loại mới!", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        setupDialogWindow(dialog);
        dialog.show();
    }

    /**
     * Popup Chỉnh sửa Thể loại (dùng dialog_edit_category.xml)
     * Các trường được điền sẵn thông tin cũ, Mã loại bị khóa cố định không cho sửa
     */
    private void showEditCategoryDialog(Category category) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_category, null);
        dialog.setContentView(dialogView);

        EditText etEditCategoryCode = dialogView.findViewById(R.id.etEditCategoryCode);
        EditText etEditCategoryName = dialogView.findViewById(R.id.etEditCategoryName);
        EditText etEditCategoryDesc = dialogView.findViewById(R.id.etEditCategoryDesc);
        View btnEditDialogCancel = dialogView.findViewById(R.id.btnEditDialogCancel);
        View btnEditDialogSave = dialogView.findViewById(R.id.btnEditDialogSave);

        // Điền sẵn dữ liệu cũ
        etEditCategoryCode.setText(category.getId());
        etEditCategoryName.setText(category.getName());
        etEditCategoryDesc.setText(category.getDescription());

        // Đóng popup
        btnEditDialogCancel.setOnClickListener(v -> dialog.dismiss());

        // Lưu cập nhật
        btnEditDialogSave.setOnClickListener(v -> {
            String newName = etEditCategoryName.getText().toString().trim();
            String newDesc = etEditCategoryDesc.getText().toString().trim();

            if (newName.isEmpty()) {
                etEditCategoryName.setError("Vui lòng nhập tên thể loại!");
                etEditCategoryName.requestFocus();
                return;
            }

            // Cập nhật thông tin
            category.setName(newName);
            category.setDescription(newDesc);
            categoryAdapter.updateData(categoryList);

            Toast.makeText(this, "Đã cập nhật thể loại: " + category.getId(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        setupDialogWindow(dialog);
        dialog.show();
    }

    /**
     * Popup Xác nhận Xóa thể loại (dùng dialog_delete_confirm.xml)
     */
    private void showDeleteConfirmDialog(Category category) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_confirm, null);
        dialog.setContentView(dialogView);

        TextView tvDeleteMessage = dialogView.findViewById(R.id.tvDeleteMessage);
        View btnCancelDelete = dialogView.findViewById(R.id.btnCancelDelete);
        View btnConfirmDelete = dialogView.findViewById(R.id.btnConfirmDelete);

        tvDeleteMessage.setText("Bạn có chắc chắn muốn xóa thể loại \"" + category.getName() + "\" (" + category.getId() + ") không?");

        btnCancelDelete.setOnClickListener(v -> dialog.dismiss());

        btnConfirmDelete.setOnClickListener(v -> {
            categoryList.remove(category);
            categoryAdapter.updateData(categoryList);
            Toast.makeText(this, "Đã xóa thể loại: " + category.getName(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        setupDialogWindow(dialog);
        dialog.show();
    }

    /**
     * Cấu hình hiển thị Dialog bo tròn 16dp và vừa vặn màn hình
     */
    private void setupDialogWindow(Dialog dialog) {
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.92);
            dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
