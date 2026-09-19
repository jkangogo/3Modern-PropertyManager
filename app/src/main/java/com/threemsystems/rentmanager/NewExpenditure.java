package com.threemsystems.rentmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.ArrayList;

public class NewExpenditure extends AppCompatActivity {
    private Spinner property, paymentMode;
    private EditText payee, description, payDate, amount, payRef, mobilecode;
    private Button save, close, reset;
    private String propertycode, Pay_date;
    private ProgressBar progressBar;
    private final ArrayList<spinnerItems> propertyspinnerlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expenditure);
        ScreenNav.bind(this);
        property = findViewById(R.id.spnProperty);
        paymentMode = findViewById(R.id.spnPaymode);
        payee = findViewById(R.id.etpayee);
        description = findViewById(R.id.etdesc);
        payDate = findViewById(R.id.dtePay);
        amount = findViewById(R.id.etPayAmount);
        payRef = findViewById(R.id.etPayRefNo);
        mobilecode = findViewById(R.id.etmobileCode);
        save = findViewById(R.id.btnPaymentsubmit);
        close = findViewById(R.id.btnPaymentclose);
        reset = findViewById(R.id.btnPaymentreset);
        progressBar = findViewById(R.id.paymentaddprogress);

        DateUi.bindPicker(this, payDate, iso -> Pay_date = iso);
        ReportSupport.bindChoices(paymentMode, "M-Pesa", "Cash", "Bank Transfer");
        ReportSupport.onItem(property, item -> propertycode = item.getId());
        ReportSupport.loadOwnerProperties(this, property, propertyspinnerlist);

        reset.setOnClickListener(v -> {
            payee.setText("");
            description.setText("");
            payDate.setText("");
            amount.setText("");
            payRef.setText("");
            mobilecode.setText("");
        });
        close.setOnClickListener(v -> finish());
        save.setOnClickListener(v -> save());
    }

    private void save() {
        String payeeName = ReportSupport.text(payee);
        String payAmount = ReportSupport.text(amount);
        String mode = ReportSupport.selectedText(paymentMode);
        if (!ReportSupport.filled(Pay_date, propertycode, payeeName, payAmount, mode)) {
            UiNotifier.snack(this, "All fields are required.");
            return;
        }
        ReportSupport.submit(this, progressBar, save, "save_rentalExpenditure.php",
                new String[]{"payment_date", "pay_refno", "mobilemoneycode", "payeeExp", "payment_amount", "pay_mode", "expense_desc", "property_code"},
                new String[]{Pay_date, ReportSupport.text(payRef), ReportSupport.text(mobilecode), payeeName, payAmount, mode, ReportSupport.text(description), propertycode},
                "Expense saved.", "Could not save the expense.");
    }
}
