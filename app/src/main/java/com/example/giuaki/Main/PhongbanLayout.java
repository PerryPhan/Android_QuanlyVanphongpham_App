package com.example.giuaki.Main;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.CursorWindow;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.giuaki.Databases.PhongBanDatabase;
import com.example.giuaki.Entities.PhongBan;
import com.example.giuaki.R;
import com.example.giuaki.Statistics.CapphatVPPLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PhongbanLayout extends AppCompatActivity {
    // Main Layout
    TableLayout phongban_table_list;

    Button insertBtn;
    Button editBtn;
    Button delBtn;
    Button exitBtn;

    // Navigation
    Button navPB;
    Button navNV;
    Button navVPP;
    Button navCP;

    // Dialog Layout
    Dialog phongbandialog;

    Button backBtn;
    Button yesBtn;
    Button noBtn;

    EditText inputMaPB;
    EditText inputTenPB;

    TextView showMPBError;
    TextView showTPBError;
    TextView showResult;
    TextView showConfirm;
    TextView showLabel;

    // Database Controller
    PhongBanDatabase phongbanDB;

    // Focus
    int indexofRow = -1;
    TableRow focusRow;
    TextView focusMaPB;
    TextView focusTenPB;

    // Other
    float scale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phongban_layout);
        scale = this.getResources().getDisplayMetrics().density;
        setControl();
        loadDatabase();
        setEvent();
        setNavigation();
    }

    // --------------- MAIN HELPER -----------------------------------------------------------------
    public void setCursorWindowImageSize( int B ){
        // Khai b??o m???t field m???i cho kh??? n??ng l??u h??nh ????? ph??n gi???i l???n
        try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, B); //the 100MB is the new size
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setControl() {
        phongban_table_list = findViewById(R.id.PB_table_list);
        insertBtn = findViewById(R.id.PB_insertBtn);
        editBtn = findViewById(R.id.PB_editBtn);
        delBtn = findViewById(R.id.PB_delBtn);
        exitBtn = findViewById(R.id.PB_exitBtn);

        navPB = findViewById(R.id.PB_navbar_phongban);
        navNV = findViewById(R.id.PB_navbar_nhanvien);
        navVPP= findViewById(R.id.PB_navbar_VPP);
        navCP= findViewById(R.id.PB_navbar_capphat);
    }

    public void setEvent() {
        editBtn.setVisibility(View.INVISIBLE); // turn on when click items
        delBtn.setVisibility(View.INVISIBLE);  // this too
        setEventTable(phongban_table_list);
    }

    public void setNavigation(){
        // navPB onclick none
        // navNV
        navNV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(PhongbanLayout.this, NhanvienLayout.class);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                startActivity( intent );

            }
        });
        // navVPP
        navVPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(PhongbanLayout.this, VanphongphamLayout.class);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                startActivity( intent );

            }
        });
        // navCP
        navCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhongbanLayout.this, CapphatVPPLayout.class);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                startActivity( intent );
            }

        });

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void setEventTable(TableLayout list) {
        // Log.d("count", list.getChildCount()+""); // s??? table rows + 1
        // Kh??ng c???n thay ?????i v?? ????y ch??? m???i set Event
        // Do c?? th??m 1 th???ng example ????? l??m g???c, n??n s??? row th?? lu??n lu??n ph???i + 1
        // C?? example th?? khi th??m row th?? n?? s??? theo khu??n
        for (int i = 0; i < list.getChildCount(); i++) {
            setEventTableRows((TableRow) list.getChildAt(i), list);
        }
        // Khi t???o, d??ng n l??m tag ????? th??m row
        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // H b???m 1 c??i l?? hi???n ra c??i pop up
                createDialog(R.layout.popup_phongban);
                // Control
                setControlDialog();
                // Event
                setEventDialog(v);
            }
        });
        // Khi edit
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (indexofRow != -1) {
                    // Test
                    // Toast.makeText( PhongbanLayout.this, focusRowID+"", Toast.LENGTH_LONG).show();
                    createDialog(R.layout.popup_phongban);
                    // Control
                    setControlDialog();
                    showLabel.setText("S???a ph??ng ban");
                    showConfirm.setText("B???n c?? mu???n s???a h??ng n??y kh??ng?");
                    // Event
                    setEventDialog(v);
                    inputMaPB.setText(focusMaPB.getText());
                    inputMaPB.setEnabled(false);
                    inputTenPB.setText(focusTenPB.getText());
                }
            }
        });
        // Khi delete, c?? 3 TH : n???m ??? cu???i ho???c n???m ??? ?????u ho???c ch??nh gi???a
        // N???m ??? cu???i th?? ch??? c???n x??a cu???i
        // C??n l???i th?? sau khi x??a xong th?? ph???i c???p nh???t l???i tag cho to??n b??? col
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (indexofRow != -1) {
                    // Test
                    //  Toast.makeText( PhongbanLayout.this, indexofRow+"", Toast.LENGTH_LONG).show();
                    createDialog(R.layout.popup_phongban);
                    // Control
                    setControlDialog();
                    showLabel.setText("X??a ph??ng ban");
                    showConfirm.setText("B???n c?? mu???n x??a h??ng n??y kh??ng?");
                    // Event
                    setEventDialog(v);
                    inputMaPB.setText(focusMaPB.getText());
                    inputTenPB.setText(focusTenPB.getText());
                    inputMaPB.setEnabled(false);
                    inputTenPB.setEnabled(false);

                }
            }
        });

    }

    // To set all rows to normal state, set focusRowid = -1
    public void setNormalBGTableRows(TableLayout list) {
        // 0: l?? th???ng example ???? INVISIBLE
        // N??n b???t ?????u t??? 1 -> 9
        for (int i = 1; i < list.getChildCount(); i++) {
            TableRow row = (TableRow) list.getChildAt((int) i);
            if (indexofRow != (int) row.getId())
                row.setBackgroundColor(getResources().getColor(R.color.white));
        }
//             Toast.makeText( PhongbanLayout.this, indexofRow+"", Toast.LENGTH_LONG).show();
//        Toast.makeText(PhongbanLayout.this, indexofRow + ":" + (int) list.getChildAt(indexofRow).getId() + "", Toast.LENGTH_LONG).show();
    }

    public void setEventTableRows(TableRow tr, TableLayout list) {
        tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editBtn.setVisibility(View.VISIBLE);
                delBtn.setVisibility(View.VISIBLE);
                // v means TableRow
                v.setBackgroundColor(getResources().getColor(R.color.selectedColor));
                indexofRow = (int) v.getId();
                focusRow = (TableRow) list.getChildAt(indexofRow);
                focusMaPB = (TextView) focusRow.getChildAt(0);
                focusTenPB = (TextView) focusRow.getChildAt(1);
                setNormalBGTableRows(list);
                // Testing to get id of focusable row
                //  Toast.makeText( PhongbanLayout.this, focusRowID+"", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Load from the Database to the Table Layout
    public void loadDatabase() {
        phongbanDB = new PhongBanDatabase(this);
        List<PhongBan> list = new ArrayList<>();
        TableRow tr = null;
        setCursorWindowImageSize(100 * 1024* 1024);
        list = phongbanDB.select();
        // Tag s??? b???t ?????u ??? 1 v?? ph???i c???ng th??m th???ng example ???? c?? s???n
        for (int i = 0; i < list.size(); i++) {
            tr = createRow(this, list.get(i));
            tr.setId((int) i + 1);
            phongban_table_list.addView(tr);
        }
    }


    // --------------- DIALOG HELPER -----------------------------------------------------------------
    public void createDialog(int layout) {
        phongbandialog = new Dialog(PhongbanLayout.this);
        phongbandialog.setContentView(layout);
        phongbandialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        phongbandialog.show();
    }

    public void setControlDialog() {
        backBtn = phongbandialog.findViewById(R.id.PB_backBtn);
        yesBtn = phongbandialog.findViewById(R.id.PB_yesInsertBtn);
        noBtn = phongbandialog.findViewById(R.id.PB_noInsertBtn);

        inputMaPB = phongbandialog.findViewById(R.id.PB_inputMaPB);
        inputTenPB = phongbandialog.findViewById(R.id.PB_inputTenPB);

        showMPBError = phongbandialog.findViewById(R.id.PB_showMPBError);
        showTPBError = phongbandialog.findViewById(R.id.PB_showTPBError);
        showResult = phongbandialog.findViewById(R.id.PB_showResult);
        showConfirm = phongbandialog.findViewById(R.id.PB_showConfirm);
        showLabel = phongbandialog.findViewById(R.id.PB_showLabel);
    }

    public void setEventDialog(View view) {
        //  Toast.makeText( PhongbanLayout.this, (view.getId() == R.id.PB_editBtn)+"", Toast.LENGTH_LONG).show();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phongbandialog.dismiss();
            }
        });
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phongbandialog.dismiss();
            }
        });
        // D???a v??o c??c n??t m?? th???ng yesBtn s??? c?? event kh??c
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  showMPBError.setVisibility(View.VISIBLE);
                //  showTPBError.setVisibility(View.VISIBLE);
                //  showResult.setVisibility(View.VISIBLE);
                boolean success = false;
                switch (view.getId()) {
                    case R.id.PB_insertBtn: {
                        if (!isSafeDialog( false )) break;
                        PhongBan pb = new PhongBan(inputMaPB.getText().toString().trim() + "", inputTenPB.getText().toString().trim() + "");
                        if (phongbanDB.insert(pb) == -1) break;
                        TableRow tr = createRow(PhongbanLayout.this, pb);
                        int n = phongban_table_list.getChildCount();
                        tr.setId(n);
                        phongban_table_list.addView(tr);
                        setEventTableRows((TableRow) phongban_table_list.getChildAt(n), phongban_table_list);
                        success = true;
                        editBtn.setVisibility(View.INVISIBLE);
                        delBtn.setVisibility(View.INVISIBLE);
                        focusRow = null;
                        focusMaPB = null;
                        focusTenPB = null;
                    }
                    break;
                    case R.id.PB_editBtn: {
                        if (!isSafeDialog( true )) break;
                        TableRow tr = (TableRow) phongban_table_list.getChildAt(indexofRow);
                        TextView id = (TextView) tr.getChildAt(0);
                        TextView name = (TextView) tr.getChildAt(1);
                        if(phongbanDB.update(new PhongBan(id.getText().toString().trim(), inputTenPB.getText().toString().trim())) == -1) break;
                        name.setText(inputTenPB.getText() + "");
                        success = true;

                    }
                    break;
                    case R.id.PB_delBtn: {
                        if( phongbanDB.delete( new PhongBan(focusMaPB.getText().toString().trim(), focusTenPB.getText().toString().trim()) ) == -1 ) break;
                        if (indexofRow == phongban_table_list.getChildCount() - 1) {
                            phongban_table_list.removeViewAt(indexofRow);
                        } else {
                            phongban_table_list.removeViewAt(indexofRow);
                            for (int i = 0; i < phongban_table_list.getChildCount(); i++) {
                                phongban_table_list.getChildAt(i).setId((int) i);
                            }
                        }
                        editBtn.setVisibility(View.INVISIBLE);
                        delBtn.setVisibility(View.INVISIBLE);
                        focusRow = null;
                        focusMaPB = null;
                        focusTenPB = null;
                        success = true;
                    }
                    break;
                    default:
                        break;
                }
                if (success) {
                    showResult.setText(showLabel.getText() + " th??nh c??ng !");
                    showResult.setTextColor(getResources().getColor(R.color.yes_color));
                    showResult.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            inputMaPB.setText("");
                            inputTenPB.setText("");
                            showResult.setVisibility(View.INVISIBLE);
                            phongbandialog.dismiss();
                        }
                    }, 1000);
                } else {
                    showResult.setTextColor(getResources().getColor(R.color.thoatbtn_bgcolor));
                    showResult.setText(showLabel.getText() + " th???t b???i !");
                    showResult.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    public boolean isSafeDialog( boolean allowSameID ) {
        String id, mapb, tenpb;
        // M?? PB kh??ng ???????c tr??ng v???i M?? PB kh??c v?? ko ????? tr???ng
        mapb = inputMaPB.getText().toString().trim();
        boolean noError = true;
        if (mapb.equals("")) {
            showMPBError.setText("M?? PB kh??ng ???????c tr???ng ");
            showMPBError.setVisibility(View.VISIBLE);
            noError = false;
        }else{
            showMPBError.setVisibility(View.INVISIBLE);
            noError = true;
        }

        // T??n PB kh??ng ???????c ????? tr???ng v?? kh??ng tr??ng
        tenpb = inputTenPB.getText().toString().trim();
        if (tenpb.equals("")) {
            showTPBError.setText("T??n PB kh??ng ???????c tr???ng ");
            showTPBError.setVisibility(View.VISIBLE);
            noError = false;
        }else{
            showTPBError.setVisibility(View.INVISIBLE);
            noError = true;
        }

//        Log.d("mapb_text",mapb+ "");
//        Log.d("tenpb_text",tenpb+ "");

        if( noError ) {
            for (int i = 1; i < phongban_table_list.getChildCount(); i++) {
                TableRow tr = (TableRow) phongban_table_list.getChildAt(i);
                TextView mapb_data = (TextView) tr.getChildAt(0);
                TextView tenpb_data = (TextView) tr.getChildAt(1);

//            Log.d("mapb",mapb_data.getText()+ "");
//            Log.d("tenpb",tenpb_data.getText()+ "");
//            Log.d("mapb_comp",(mapb.equals(mapb_data.getText().toString()))+ "");
//            Log.d("tenpb_comp",(tenpb.equals(tenpb_data.getText().toString()))+ "");

                if (!allowSameID)
                    if (mapb.equalsIgnoreCase(mapb_data.getText().toString())) {
                        showMPBError.setText("M?? PB kh??ng ???????c tr??ng ");
                        showMPBError.setVisibility(View.VISIBLE);
                        return noError = false;
                    }
                if (tenpb.equalsIgnoreCase(tenpb_data.getText().toString())
                        && !tenpb_data.getText().toString().equalsIgnoreCase(
                        focusTenPB.getText().toString().trim() )
                    ) {
                    showTPBError.setText("T??n PB kh??ng ???????c tr??ng");
                    showTPBError.setVisibility(View.VISIBLE);
                    return noError = false;
                }
            }
            showMPBError.setVisibility(View.INVISIBLE);
            showTPBError.setVisibility(View.INVISIBLE);
        }
        return noError;
    }

    // --------------- CUSTOM HELPER --------------------------------------------------------------------
    public int DPtoPix(int dps) {
        return (int) (dps * scale + 0.5f);
    }

    // This Custom Columns' Max Width : 80 / 300
    public TableRow createRow(Context context, PhongBan pb) {
        TableRow tr = new TableRow(context);
        // Id


        //   Ma PB
        TextView maPB = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // C???n c??i n??y ????? khi m?? maPB ?????t t???i max width th?? n?? s??? t??ng height cho b??n tenPB lu??n
        // L??u ??!! : khi ?????t LayoutParams th?? ph???i theo th???ng c??? n???i v?? ph???i c?? weight
        maPB.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        maPB.setMaxWidth(DPtoPix(80));
        maPB.setText(pb.getMapb());

        //   Ten PB
        TextView tenPB = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // C???n c??i n??y ????? khi m?? tenPB ?????t t???i max width th?? n?? s??? t??ng height cho b??n maPB lu??n
        tenPB.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        tenPB.setText(pb.getTenpb());
        tenPB.setMaxWidth(DPtoPix(300));

        tr.setBackgroundColor(getResources().getColor(R.color.white));
        // Add 2 th??? v??o row
        tr.addView(maPB);
        tr.addView(tenPB);

        return tr;
    }


//    public void init(){
//        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        //Here you can set up the layoutparameters from your tableview and the rowview.
//
//        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
//        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
//
//        //Here were we take the tablelayout from your xml
//        TableLayout tableLayout = (TableLayout)inflater.inflate(R.layout.activity_phongban_layout, null);
////        TableLayout tableLayout = (TableLayout)findViewById(R.id.phongban_table);
//
//        //Like a told you before, maybe you don't need set the parameters of the tablelayout
//        //so you can comment next line.
//        tableLayout.setLayoutParams(tableParams);
//
//        TableRow tableRow = new TableRow(PhongbanLayout.this);
//        tableRow.setLayoutParams(tableParams);
//
//        //Here you have to create and modify the new textview.
//        TextView textView = new TextView(PhongbanLayout.this);
//        textView.setLayoutParams(rowParams);
//
//        tableRow.addView(textView);
//        tableLayout.addView(tableRow);
//    }
}