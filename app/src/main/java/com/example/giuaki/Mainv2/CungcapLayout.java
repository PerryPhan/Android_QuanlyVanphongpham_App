package com.example.giuaki.Mainv2;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.giuaki.Adapter.TTAdapter;
import com.example.giuaki.Api.NhaCungCap;
import com.example.giuaki.Api.PhieuCungCap;
import com.example.giuaki.Api.VanPhongPham;
import com.example.giuaki.Helper.JSONHelper;
import com.example.giuaki.R;
import com.example.giuaki.Request.PhieuCungCapRequest;
import com.example.giuaki.Request.VanPhongPhamRequest;
import com.example.giuaki.Statistics.CapphatVPPLayout;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CungcapLayout extends AppCompatActivity {
    // Main Layout
    TableLayout cc_table_list;

    Button  detailBtn,
            insertBtn,
            editBtn,
            delBtn;

    Spinner NCCSpinner,
            TTSpinner;
    String NCCSpinner_data = null;
    String TTSpinner_data = null;

    // Database
    PhieuCungCapRequest phieucungcapDB;
    VanPhongPhamRequest vanphongphamDB;

    // List
    String[] trangthai = {"OPENNING","CONFIRMED","DELIVERED","CANCELED"};
    List<NhaCungCap> NCCList = null;
    List<PhieuCungCap> PCCList = null;
    List<PhieuCungCap> sortPCCList = null;
    List<String> tenNCCList = null;
    List<String> maNCCList = null;
    List<String> tenTTList = null;


    // Focus
    int indexofRow = -1;
    TableRow focusRow;
    TextView focusSoPhieu;
    TextView focusNgaygiao;
    TextView focusNCC;
    TextView focusTongtien;
    String focusTT;
    // Dialog
    Dialog dialog;
        Button backBtn;
        Button yesBtn;
        Button noBtn;

        Spinner NCCSpinner_mini,
                TTSpinner_mini;
        String NCCSpinner_mini_data = null;
        String TTSpinner_mini_data = null;

        List<String> tenNCCList_mini = null;
        List<String> maNCCList_mini = null;
        List<String> tenTTList_mini = null;

        EditText inputSP;
        DatePicker inputND;
        String inputSP_data = null;
        String inputND_data = null;

        TextView showSPError;
        TextView showNGError;

        TextView showResult;
        TextView showConfirm;
        TextView showLabel;

    // Navigation
    Button navPB,
           navNV,
           navVPP,
            navCP,
            exit;
    // Other
    float scale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scale = this.getResources().getDisplayMetrics().density;
        setContentView(R.layout.activity_cungcap_layout);
        setControl();
        loadDatabase();
        setEvent();
        setNavigation();
    }

    private void setControl() {
        // Spinner
        NCCSpinner = findViewById(R.id.CC_NCCSpinner);
        TTSpinner = findViewById(R.id.CC_TTSpinner);
        // Table
        cc_table_list = findViewById(R.id.CC_table_list);
        // Btn
        detailBtn = findViewById(R.id.CC_detailBtn);
        insertBtn = findViewById(R.id.CC_insertBtn);
        editBtn  = findViewById(R.id.CC_editBtn);
        delBtn = findViewById(R.id.CC_delBtn);
        exit = findViewById(R.id.CC_editBtn);
        // Navigation
        navPB = findViewById(R.id.CC_navbar_phongban);
        navNV = findViewById(R.id.CC_navbar_nhanvien);
        navVPP= findViewById(R.id.CC_navbar_VPP);
        navCP= findViewById(R.id.CC_navbar_capphat);
    }
    // --------------------------------- {{}} ----------------------------------
    // --------------------------------- LOADER ----------------------------------
    private void loadDatabase() {
        // Khai báo
        vanphongphamDB = new VanPhongPhamRequest();
        phieucungcapDB = new PhieuCungCapRequest();
        // Đổ Database ra List
        NCCList = convertToNhaCungCapList(
                returnListfromJSON(
                        vanphongphamDB.getNCCList()
                        , "NhaCungCap"
                )
        );
//        sortPCCList = PCCList = convertToPhieuCungCapList( // Tạo 1 bản copy của PCCList để dễ sort
//                returnListfromJSON(
//                        phieucungcapDB.doGet("show")
//                        , "PhieuCungCap"
//                )
//        );
        sortPCCList = PCCList = exampleList();
        tenTTList = TrangThaiList( trangthai, true, "");
        tenNCCList = NhaCungCapList( NCCList, true);
        maNCCList = maNhaCungCapList( NCCList, true);
        // Load list vào Spinner
        NCCSpinner.setAdapter( loadSpinnerAdapter((ArrayList<String>) tenNCCList) );
        TTSpinner.setAdapter(
                new TTAdapter(this, tenTTList)
        );
        // Load List vào TableList
        if( PCCList == null ) {
            Log.d("data",
                "CuncapLayout/PCCList in LoadDatabase() is null");
            return;
        }
        renderTablefromList(sortPCCList);

    }
    public List<PhieuCungCap> exampleList(){
        List<PhieuCungCap> list = new ArrayList<>();
        // OPENNING
        list.add( new PhieuCungCap("SP01","OPENNING","VPPKBTC"));
        list.add( new PhieuCungCap("SP02","OPENNING","VPPSH"));
        // CONFIRMED
        list.add( new PhieuCungCap("SP03","CONFIRMED","VPPVNC"));
        list.add( new PhieuCungCap("SP04","CONFIRMED","VPPVNC"));
        // DELIVERED
        list.add( new PhieuCungCap("SP05","DELIVERED","VPPSH"));
        list.add( new PhieuCungCap("SP06","DELIVERED","VPPSH"));
        // CANCELED
        list.add( new PhieuCungCap("SP07","CANCELED","VPPKBTC"));
        list.add( new PhieuCungCap("SP08","CANCELED","VPPKBTC"));

        return list;
    }
    public List<NhaCungCap> convertToNhaCungCapList(List<Object> list ){
        if( list == null ) return null;
        List<NhaCungCap> nhacuncaplist = new ArrayList<>();
        for( Object li : list ){
            nhacuncaplist.add( (NhaCungCap) li);
        }
        return nhacuncaplist;
    }
    public List<PhieuCungCap> convertToPhieuCungCapList(List<Object> list ){
        if( list == null ) return null;
        List<PhieuCungCap> phieucuncaplist = new ArrayList<>();
        for( Object li : list ){
            phieucuncaplist.add( (PhieuCungCap) li);
        }
        return phieucuncaplist;
    }
    public List<Object> returnListfromJSON( String resultfromQuery , String objectClass){
        List<Object> list = null ;
        String response = resultfromQuery;
        if( !JSONHelper.verifyJSON(response).equalsIgnoreCase("pass") ) return null;
        try{
            list = JSONHelper.parseJSON(response,objectClass);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return list;
        }
    }

    private void testData(ArrayList<PhieuCungCap> data){
        for( PhieuCungCap d : data){
            Log.d("data",d.getSoPhieu());
        }
    }

    private List<String> NhaCungCapList( List<NhaCungCap> ncc, boolean isAll ){
        if( ncc == null ) return null;
        List<String> nccList = new ArrayList<>();
        if( isAll ) nccList.add(0, "Tất cả nhà cung cấp");
        for( NhaCungCap n : ncc){
            nccList.add(n.getTenNCC());
        }
        return nccList;
    }
    private List<String> maNhaCungCapList( List<NhaCungCap> ncc, boolean isAll ){
        if( ncc == null ) return null;
        List<String> nccList = new ArrayList<>();
        if( isAll ) nccList.add(0, "Tất cả nhà cung cấp");
        for( NhaCungCap n : ncc){
            nccList.add(n.getMaNCC());
        }
        return nccList;
    }
    private List<String> TrangThaiList( String[] tt , boolean isAll, String mode) {
        if(tt == null) return null;
        List<String> TTList = new ArrayList<>();
        if( isAll ) TTList.add("Tất cả trạng thái");

        for( String tentt : tt){
            if( mode.equals("OPENNING")) {
                if ( tentt.equals("OPENNING"))
                    TTList.add(tentt);
            }else
                TTList.add(tentt);
        }

        return TTList;
    }
    public ArrayAdapter<String> loadSpinnerAdapter(ArrayList<String> tenNCClist) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tenNCClist);
        return adapter;
    }
    // --------------------------------- {{}} ----------------------------------

    // ----------------------------------{{}} ----------------------------------
    // -------------------------------- EVENTER --------------------------------
    private void setEvent() {
//        Event cho các nút
        setEventButton();
//        Event cho Spinner
        setEventNCCSpinner();
        setEventTTSpinner();
    }

    // ---------------------------- EVENT ON SPINNER -----------------------
    public void renderTablefromList( List<PhieuCungCap> list ){
//        Clear from 1 to n-1
        if( list == null || list.size() == 0 ) return;
        int n = cc_table_list.getChildCount();
        if(n > 1)
            cc_table_list.removeViews(1, n-1 );
        int dem = 0;
        for( PhieuCungCap pcc: list ){
            TableRow tr = createRow(this, pcc);
            tr.setId( ++dem );
            cc_table_list.addView(tr);
        }
        setEventTableList();
    }
    public List<PhieuCungCap> filterTableListwith( String tentt, String field, List<PhieuCungCap> list ) {
        if (list == null || list.size() == 0) {
            Log.d("data", "Table has no data in filterTableListwith");
            return null;
        }
        if (tentt == null || tentt.trim().equalsIgnoreCase("")) return null;
        if (field == null || field.trim().equalsIgnoreCase("")) return null;
        List<PhieuCungCap> filtered_list = new ArrayList<>();
        if (field.equalsIgnoreCase("tt")){
            for (PhieuCungCap pcc : list) {
                if (pcc.getTrangThai().equals(tentt)) {
                    filtered_list.add(pcc);
                }
            }
        }
        if( field.equalsIgnoreCase("ncc")) {
            for (PhieuCungCap pcc : list) {
                if (pcc.getMaNcc().equals(tentt)) {
                    filtered_list.add(pcc);
                }
            }
        }
        return filtered_list;
    }
    private void setEventTTSpinner() {
        // Khi chọn TT, bảng sẽ lọc theo TT
        // VD: có dữ liệu là như thế
        TTSpinner_data = "All";
        TTSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(  position == 0 ){
                    if( NCCSpinner_data.equals("All"))
                        sortPCCList = PCCList;
                    else
                        sortPCCList = filterTableListwith(
                                NCCSpinner_data,
                                "ncc",
                                PCCList
                        );
                    TTSpinner_data = "All";
                    renderTablefromList(sortPCCList);
                }else{
                    // Nghĩa là filter này sẽ là filter dựa trên bảng đã filter bên NCC
                    if( NCCSpinner_data.equals("All"))
                        sortPCCList = PCCList;
                    else
                        sortPCCList = filterTableListwith(
                                NCCSpinner_data,
                                "ncc",
                                PCCList
                        );
                    TTSpinner_data = tenTTList.get(position);
                    sortPCCList = filterTableListwith(TTSpinner_data , "tt", sortPCCList);
                    renderTablefromList(sortPCCList);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TTSpinner_data = "All";
            }
        });
    }
    private void setEventNCCSpinner() {
        // Khi chọn NCC, bảng sẽ lọc theo NCC
        NCCSpinner_data = "All";
        NCCSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if( position == 0 ){
                    if( TTSpinner_data.equals("All"))
                        sortPCCList = PCCList;
                    else
                        sortPCCList = filterTableListwith(
                                TTSpinner_data,
                                "tt",
                                PCCList
                        );
                    NCCSpinner_data = "All";
                    renderTablefromList(sortPCCList);
                }else{
                    // Nghĩa là filter này sẽ là filter dựa trên bảng đã filter bên TT
                    if( TTSpinner_data.equals("All"))
                        sortPCCList = PCCList;
                    else
                        sortPCCList = filterTableListwith(
                                TTSpinner_data,
                                "tt",
                                PCCList
                        );
                    NCCSpinner_data = maNCCList.get(position);
                    sortPCCList = filterTableListwith(NCCSpinner_data,"ncc",sortPCCList);
                    renderTablefromList(sortPCCList);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                NCCSpinner_data = "All";
            }
        });
    }
    // ---------------------------- EVENT ON TABLE -----------------------
    public void setNormalBGTableRows(TableLayout list) {
        for (int i = 1; i < list.getChildCount(); i++) {
            TableRow row = (TableRow) list.getChildAt((int) i);
            if (indexofRow != (int) row.getId())
                row.setBackgroundColor(getResources().getColor( chooseColor( (String) row.getTag() ) ));
        }
    }
    public int chooseColorFocus( String tag ){
        switch (tag){
            case "OPENNING": // Cho phép tất cả
                return R.color.focus_openning_color;
            case "CONFIRMED":
                return R.color.focus_confirmed_color;
            case "DELIVERED":
                return R.color.focus_delivered_color;
            case "CANCELED":
                return R.color.focus_cancel_color;
            default:
                return R.color.white;
        }
    }
    public int chooseColor( String tag ){
        switch (tag){
            case "OPENNING": // Cho phép tất cả
                return R.color.openning_color;
            case "CONFIRMED":
                return R.color.confirmed_color;
            case "DELIVERED":
                return R.color.delivered_color;
            case "CANCELED":
                return R.color.cancel_color;
            default:
                return R.color.white;
        }
    }
    public void setEventTableRows(TableRow tr, TableLayout list) {
        tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusTT = (String) v.getTag();
                showButton( focusTT );
                // v means TableRow
                v.setBackgroundColor( getResources().getColor( chooseColorFocus(focusTT) ) );
                indexofRow = (int) v.getId();
                focusRow = (TableRow) list.getChildAt(indexofRow);
                focusSoPhieu = (TextView) focusRow.getChildAt(0);
                focusNgaygiao = (TextView) focusRow.getChildAt(1);
                focusNCC = (TextView) focusRow.getChildAt(2);
                focusTongtien = (TextView) focusRow.getChildAt(3);
                setNormalBGTableRows( list );
            }
        });
    }
    private void setEventTableList() {
        int n = cc_table_list.getChildCount();
        for( int i = 1; i < n; i++){
            TableRow tr = (TableRow) cc_table_list.getChildAt(i);
            setEventTableRows( tr , cc_table_list );
        }
    }

    // ---------------------------- EVENT ON BUTTON -----------------------
    private void hideButton() {
        delBtn.setVisibility(View.INVISIBLE);
        editBtn.setVisibility(View.INVISIBLE);
        detailBtn.setVisibility(View.INVISIBLE);
    }
    private void showButton( String mode ) {
        hideButton();
        detailBtn.setVisibility(View.VISIBLE);
        switch (mode){
            case "OPENNING": // Cho phép tất cả
                delBtn.setVisibility(View.VISIBLE);
                editBtn.setVisibility(View.VISIBLE);
                break;
            case "CONFIRMED":
                editBtn.setVisibility(View.VISIBLE);
                break;
            case "DELIVERED":
                break;
            case "CANCELED":
                break;
            default:
                break;
        }
    }
    public String getDate( String mode){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        if( !mode.equalsIgnoreCase("tommorrow")) return formatter.format(date);
        Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DATE, 1);
            date = c.getTime();
        return formatter.format(date);
    }
    public void setEventDatePicker( String init ){
        inputND_data = init;
        int[] date = StringtoIntDate( formatDate( init, false));
        inputND.init(date[2],date[1]-1,date[2],new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                inputND_data = formatDate(InttoStringDate(dayOfMonth,monthOfYear+1,year), true);
//                 Toast.makeText( NhanvienLayout.this, strDate+"", Toast.LENGTH_LONG).show();
            }

        });
    }
    private void setEventButton() {
        detailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CungcapLayout.this, CTCungcapLayout.class);
                Bundle b = new Bundle();
                    b.putString("sp",focusSoPhieu.getText().toString().trim()+"");
                    b.putString("ng",focusNgaygiao.getText().toString().trim()+"");
                    b.putString("ncc",focusNCC.getText().toString().trim()+"");
                    b.putString("ttien",focusTongtien.getText().toString().trim()+"");
                    b.putString("tt",focusTT);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                intent.putExtras(b);
                startActivity( intent );
            }
        });
        int layout = R.layout.popup_cungcap ;
        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(layout);
                setControlDialog();
                // Access Control
                // none
                setEventDialog();
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(layout);
                setControlDialog();
                // Access Control
                inputSP.setEnabled(false);
                switch ( focusTT ){
                    case "OPENNING":
                    {
                        String temp = (String) focusTongtien.getText();
                        int tongtien = Integer.parseInt(
                                (String) temp.subSequence(0, temp.length()-1 )
                        );
                        // Đã có detail
                        if ( tongtien != 0 ){
                            NCCSpinner_mini.setEnabled(false);
                        }
                        setEventDatePicker( getDate("tomorrow") );
                        tenTTList_mini = TrangThaiList( trangthai, false, focusTT);
                        tenNCCList_mini = NhaCungCapList( NCCList, false);
                        maNCCList_mini = maNhaCungCapList( NCCList, false);
                        NCCSpinner_mini.setAdapter( loadSpinnerAdapter((ArrayList<String>) tenTTList_mini) );
                        TTSpinner_mini.setAdapter(
                                new TTAdapter(CungcapLayout.this, tenTTList_mini)
                        );
                    }
                    break;
                    case "CONFIRMED":
                        NCCSpinner_mini.setEnabled(false);
                        setEventDatePicker( (String) focusNgaygiao.getText() );
                        break;
                    default: break;
                }
                showLabel.setText("Sửa phiếu cung cấp");
                showConfirm.setText("Bạn có muốn sửa hàng này không?");
                setEventDialog();
            }
        });
        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(layout);
                setControlDialog();
                // Access Control
                showLabel.setText("Xóa phiếu cung cấp");
                showConfirm.setText("Bạn có muốn sửa hàng này không?");
                setEventDialog();
            }
        });
    }
    // ----------------------------------{{}} ----------------------------------

    // ---------------------------- DIALOG -----------------------------------
    // --------------- DIALOG HELPER -----------------------------------------------------------------
    public void createDialog(int layout) {
        dialog = new Dialog(CungcapLayout.this);
        dialog.setContentView(layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void setControlDialog() {
        backBtn = dialog.findViewById(R.id.CC_backBtn);
        yesBtn = dialog.findViewById(R.id.CC_yesInsertBtn);
        noBtn = dialog.findViewById(R.id.CC_noInsertBtn);

        NCCSpinner_mini = dialog.findViewById(R.id.CC_NCCSpinner_mini);

        TTSpinner_mini = dialog.findViewById(R.id.CC_TTSpinner_mini);

        inputSP = dialog.findViewById(R.id.CC_inputSP);
        inputND = dialog.findViewById(R.id.CC_inputNG);

        showSPError = dialog.findViewById(R.id.CC_showSPError);
        showNGError = dialog.findViewById(R.id.CC_showNGError);

        showResult = dialog.findViewById(R.id.VPP_showResult);
        showConfirm = dialog.findViewById(R.id.VPP_showConfirm);
        showLabel = dialog.findViewById(R.id.VPP_showLabel);
    }

    public void setEventDialog(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean success = false;
                switch (v.getId()) {
                    case R.id.CC_insertBtn:

                        break;
                    case R.id.CC_editBtn:

                        break;
                    case R.id.CC_delBtn:

                        break;
                }
                if (success) {
                    showResult.setText(showLabel.getText() + " thành công !");
                    showResult.setTextColor(getResources().getColor(R.color.yes_color));
                    showResult.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showResult.setVisibility(View.INVISIBLE);
                            dialog.dismiss();
                        }
                    }, 1000);
                } else {
                    showResult.setTextColor(getResources().getColor(R.color.thoatbtn_bgcolor));
                    showResult.setText(showLabel.getText() + " thất bại !");
                    showResult.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    // ---------------------------- NAVIGATION  -----------------------
    private void setNavigation() {
        // navVPP onclick none
        // navPB
        navPB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(CungcapLayout.this, PhongbanLayout.class);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                startActivity( intent );
            }
        });
        // navNV
        navNV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(CungcapLayout.this, NhanvienLayout.class);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                startActivity( intent );

            }
        });
        // navVPP
        navVPP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CungcapLayout.this, VanphongphamLayout.class);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                startActivity( intent );
            }
        });
        // navCP
        navCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CungcapLayout.this, CapphatVPPLayout.class);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                startActivity( intent );
            }

        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    // --------------- CUSTOM HELPER --------------------------------------------------------------------
    public int DPtoPix(int dps) {
        return (int) (dps * scale + 0.5f);
    }
    // This Custom Columns' Max Width : 80 p0 / 120 / 85 p0 / <= 100 p0
    public TableRow createRow(Context context, PhieuCungCap pcc) {
        TableRow tr = new TableRow(context);
        //  SoPhieuview
        TextView SoPhieuview = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà SoPhieuview đạt tới max width thì nó sẽ tăng height cho bên tenVPP luôn
        // Lưu ý!! : khi đặt LayoutParams thì phải theo thằng cố nội và phải có weight
        SoPhieuview.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        SoPhieuview.setMaxWidth(DPtoPix(80));
        SoPhieuview.setPadding(0,0,0,0);
        SoPhieuview.setText(pcc.getSoPhieu());

        TextView NgayGiaoview = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà NgayGiaoview đạt tới max width thì nó sẽ tăng height cho bên tenVPP luôn
        // Lưu ý!! : khi đặt LayoutParams thì phải theo thằng cố nội và phải có weight
        NgayGiaoview.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        NgayGiaoview.setMaxWidth(DPtoPix(120));
        NgayGiaoview.setText(formatDate("2021-06-02",false));

        TextView NhaCungCapview = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà NhaCungCapview đạt tới max width thì nó sẽ tăng height cho bên tenVPP luôn
        // Lưu ý!! : khi đặt LayoutParams thì phải theo thằng cố nội và phải có weight
        NhaCungCapview.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        NhaCungCapview.setMaxWidth(DPtoPix(120));
        NhaCungCapview.setText(pcc.getMaNcc());

        TextView TongTienview = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
        // Cần cái này để khi mà TongTienview đạt tới max width thì nó sẽ tăng height cho bên tenVPP luôn
        // Lưu ý!! : khi đặt LayoutParams thì phải theo thằng cố nội và phải có weight
        TongTienview.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
        TongTienview.setMaxWidth(DPtoPix(120));
        TongTienview.setText("0đ");

        int color = R.color.white;
        String tag = pcc.getTrangThai();

        tr.setTag(tag);
        tr.setBackgroundColor(getResources().getColor( chooseColor(tag) ));
        tr.addView(SoPhieuview);
        tr.addView(NgayGiaoview);
        tr.addView(NhaCungCapview);
        tr.addView(TongTienview);
        return tr;
    }

    public int[] StringtoIntDate(String str) {
        int[] date = new int[3];
        String[] arr = str.split("/");
        date[0] = Integer.parseInt(arr[0]);
        date[1] = Integer.parseInt(arr[1]);
        date[2] = Integer.parseInt(arr[2]);
        return date; // 30/08/1999 -> [30,08,1999]
    }

    public String InttoStringDate(int[] date) {
        String day = (date[0] < 10) ? '0' + date[0] + "" : date[0] + "";
        String month = (date[1] < 10) ? '0' + date[1] + "" : date[1] + "";
        String year = date[2] + "";
        return day + "/" + month + "/" + year; // [30,08,1999] -> 30/08/1999
    }

    public String InttoStringDate(int date_day, int date_month, int date_year) {
        Log.d("day",date_day+"");
        String day = (date_day < 10) ? "0" + date_day + "" : date_day + "";
        String month = (date_month < 10) ? "0" + date_month + "" : date_month + "";
        String year = date_year + "";
        return day + "/" + month + "/" + year; // [30,08,1999] -> 30/08/1999
    }

    public String formatDate(String str, boolean toSQL ){
        String[] date ;
        String result = "";
        if( toSQL ){
            date = str.split("/");
            result = date[2] +"-"+ date[1] +"-"+ date[0];
        }else{
            date = str.split("-");
            result = date[2] +"/"+ date[1] +"/"+ date[0];
        }

        return result;
    }

    public String MoneyFormat( int money ){
        if( money == 0) return "0đ";
        int temp_money = money;
        String moneyFormat = "";
        if( money < 1000) return String.valueOf(money) +" đ";
        else {
            int count = 0;
            while (temp_money != 0) {
                moneyFormat += (temp_money % 10) + "";
                if ((count + 1) % 3 == 0 && temp_money > 10) moneyFormat += ".";
                count++;
                temp_money /= 10;
            }
        }
        return new StringBuilder(moneyFormat).reverse().toString() +" đ";
    }

}