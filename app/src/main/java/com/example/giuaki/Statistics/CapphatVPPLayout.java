package com.example.giuaki.Statistics;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.giuaki.Databases.CapPhatDatabase;
import com.example.giuaki.Databases.NhanVienDatabase;
import com.example.giuaki.Databases.PhongBanDatabase;
import com.example.giuaki.Databases.VanPhongPhamDatabase;
import com.example.giuaki.Entities.CapPhat;
import com.example.giuaki.Entities.NhanVien;
import com.example.giuaki.Entities.PhongBan;
import com.example.giuaki.Entities.VanPhongPham;
import com.example.giuaki.R;

import java.util.ArrayList;
import java.util.List;

public class CapphatVPPLayout extends AppCompatActivity {
    // Main Layout
    Button backBtn;

    Spinner PBSpinner;

    LinearLayout cp_tablesall_container;
    LinearLayout cp_tablesindex_container;
    TableLayout cp_tablevpp_list;
    TableLayout cp_tablenv_list;
    TableLayout cp_tablecp_list;

    TextView warningLabel;
    TextView labelVPP;
    TextView totalCount;
    TextView totalPrice;

    Button previewVPPBtn;
    Button navBC;
    Button navTK;

    // Data
    CapPhatDatabase capphatDB ;
    NhanVienDatabase nhanvienDB;
    VanPhongPhamDatabase vanphongphamDB;

    List<CapPhat> capphat_list;
    List<NhanVien> nhanvien_list;
    List<VanPhongPham> vanphongpham_list;
    List<PhongBan> phongban_list;

    // Dialog
    Dialog dialog;
        // Preview Image Layout
        TextView VPP_IP_maVPP;
        TextView VPP_IP_tenVPP;
        TextView VPP_IP_DVT;
        TextView VPP_IP_Gia;
        ImageView VPP_IP_Hinh;

    // Focus
    TableRow focusRow;
    TextView focusSP;
    TextView focusDate;
    TextView focusMaVPP;
    TextView focusMaNV;
    TextView focusSL;
    String dataMaPBSpinner;

    // Other
    float scale;
    int indexofRow = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capphatvpp_layout);
        scale = this.getResources().getDisplayMetrics().density;
        setControl();
        loadDatabase();
        setEvent();
        setNavigation();
        // Testing Query
        List<String> cp_vpp_nvlist = capphatDB.selectwithVPPandNVwherePB("PB01");
        String row = "";
        int count = 0;
        for( String str : cp_vpp_nvlist){
//            Log.d("data", str);
            if( count == 3) {
                count = 0;
                Log.d("data", row+"\n");
                row = "";
            }else{
                count++;
                row += str+", ";
            }
        }
    }

    // --------------- MAIN HELPER -----------------------------------------------------------------
    public void setControl() {
//        Log.d("process", "setControl");
        backBtn = findViewById(R.id.CP_backBtn);

        PBSpinner = findViewById(R.id.CP_PBSpinner);

        cp_tablesall_container = findViewById( R.id.CP_tablesAll_container );
        cp_tablesindex_container = findViewById( R.id.CP_tablesIndex_container );
        cp_tablevpp_list = findViewById(R.id.CP_tableVPP);
        cp_tablenv_list = findViewById(R.id.CP_tableNV);
        cp_tablecp_list = findViewById(R.id.CP_tableCP);

        warningLabel = findViewById(R.id.CP_warningLabel);
        totalCount = findViewById(R.id.CP_totalCount);
        totalPrice = findViewById(R.id.CP_totalPrice);
        labelVPP = findViewById(R.id.CP_labelVPP);

        previewVPPBtn = findViewById(R.id.CP_previewVPPBtn);
        navBC = findViewById(R.id.CP_navbar_baocao);
        navTK = findViewById(R.id.CP_navbar_thongke);
    }

    public void loadDatabase(){
        //   Log.d("process", "loadDatabase");
        // 1.  Load Spinner ra trước
        capphatDB = new CapPhatDatabase(CapphatVPPLayout.this);
        capphat_list = capphatDB.select();
        for( int i = 0; i < capphat_list.size(); i++){
            CapPhat cp = capphat_list.get(i);
            TableRow tr = createRow(CapphatVPPLayout.this,cp);
            tr.setId( i+1 );
            cp_tablecp_list.addView( tr );
//            Log.d("data",cp.toString()+"");
        }
        nhanvienDB = new NhanVienDatabase( CapphatVPPLayout.this );
        nhanvien_list = nhanvienDB.select();
        for( int i = 0; i < nhanvien_list.size(); i++){
            NhanVien nv = nhanvien_list.get(i);
            TableRow tr = createRow(CapphatVPPLayout.this,nv);
            tr.setId( i+1 );
            cp_tablenv_list.addView( tr );
//            Log.d("data",nv.toString()+"");
        }
        vanphongphamDB = new VanPhongPhamDatabase( CapphatVPPLayout.this );
        vanphongpham_list = vanphongphamDB.select();
        PBSpinner.setAdapter( loadPBSpinner() );

//        List<String> listcau2a = capphatDB.thongKeCau2a();
//        Log.d("count",listcau2a.size() /5 +"");
//        for( String str : listcau2a){
//            Log.d("count",str +"");
//        }
//        List<String> listcau2b = capphatDB.thongKeCau2a();
    }

    public void setEvent(){
        labelVPP.setVisibility(View.INVISIBLE);
        previewVPPBtn.setVisibility( View.INVISIBLE );
        // 1. Set Event cho Spinner
        setEventPBSpinner();
        // 2. Set Event Table Rows cho Table _CP và Table _NV simple
        for (int i = 0; i < cp_tablecp_list.getChildCount(); i++) {
            setEventTableRows((TableRow) cp_tablecp_list.getChildAt(i), cp_tablecp_list);
        }
//        for (int i = 0; i < cp_tablenv_list.getChildCount(); i++) {
//            setEventTableRows((TableRow) cp_tablenv_list.getChildAt(i), cp_tablenv_list);
//        }

    }

    public void setNavigation(){
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        navBC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   new BaoCao
            }
        });
        navTK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   new ThongKe
            }
        });
    }

    public void transferLayout( String maPB ){
        if( maPB.trim().equalsIgnoreCase("")) return;
        // 1. maPB là all thì chuyển sang layout maPB
        switch (maPB){
            case "All" : {
                warningLabel.setText("Khi chọn phòng ban cụ thể, cấu trúc bảng sẽ khác");
                // All : show
                cp_tablesall_container.setLayoutParams( new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT )
                );
                // Index : hide
                cp_tablesindex_container.setLayoutParams( new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0 )
                );
            };
                break;
            default: {
                warningLabel.setText("Khi chọn tất cả phòng ban, cấu trúc bảng sẽ khác");
                // All : hide
                cp_tablesall_container.setLayoutParams( new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        0 )
                );
                // Index : show
                cp_tablesindex_container.setLayoutParams( new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT )
                );

            }; break;
        }
    }

    public ArrayAdapter<String> loadPBSpinner(){
        // 1. Tạo list Phong ban // 2. Đổ Phong_ban.getTenPB() ra 1 List // 3. setAdapter cho cái list getTenPB() đó
        phongban_list = new PhongBanDatabase(CapphatVPPLayout.this).select();
        ArrayList<String> phongbanNames_list = new ArrayList<>();
        phongbanNames_list.add("Tất cả phòng ban");
        // Phục vụ cho việc xổ ra Option cho Spinner
        for ( PhongBan pb : phongban_list){
            phongbanNames_list.add(pb.getTenpb());
//            Log.d("data", pb.getTenpb());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, phongbanNames_list);
        return adapter;
    }

    public void setEventPBSpinner(){
        PBSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0) dataMaPBSpinner = "All";
                else {
                    // 1.
                    dataMaPBSpinner = phongban_list.get(position - 1).getMapb();
                }
                transferLayout( dataMaPBSpinner );
                Toast.makeText( CapphatVPPLayout.this, dataMaPBSpinner+"", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                dataMaPBSpinner = "All";
            }
        });
    }

    // To set all rows to normal state, set focusRowid = -1
    public void setNormalBGTableRows(TableLayout list) {
        // 0: là thằng example đã INVISIBLE
        // Nên bắt đầu từ 1 -> 9
        for (int i = 1; i < list.getChildCount(); i++) {
            TableRow row = (TableRow) list.getChildAt((int) i);
            if (indexofRow != (int) row.getId())
                row.setBackgroundColor(getResources().getColor(R.color.white));
        }
//             Toast.makeText( PhongbanLayout.this, indexofRow+"", Toast.LENGTH_LONG).show();
        Toast.makeText(CapphatVPPLayout.this, indexofRow + ":" + (int) list.getChildAt(indexofRow).getId() + "", Toast.LENGTH_LONG).show();
    }

    public void setEventTableRows(TableRow tr, TableLayout list) {
        tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // v means TableRow
                v.setBackgroundColor(getResources().getColor(R.color.selectedColor));
                indexofRow = (int) v.getId();
                focusRow = (TableRow) list.getChildAt(indexofRow);
                focusSP = (TextView) focusRow.getChildAt(0);
                focusDate = (TextView) focusRow.getChildAt(1);
                focusMaVPP = (TextView) focusRow.getChildAt(2);
                focusMaNV = (TextView) focusRow.getChildAt(3);
                focusSL = (TextView) focusRow.getChildAt(4);
                setNormalBGTableRows(list);
                setEventTableRowsHelper( cp_tablenv_list );
                setEventDisplayVPP( focusMaVPP.getText().toString().trim() );
                // Testing to get id of focusable row
                //  Toast.makeText( PhongbanLayout.this, focusRowID+"", Toast.LENGTH_LONG).show();
            }
        });
    }

    public int findMaNVinTableNV( TableLayout list ){
        TableRow tr = null;
        TextView maNV = null;
        if( focusMaNV == null) return -1;
        Log.d("focus",focusMaNV.getText()+"");
        for( int i = 1; i < list.getChildCount(); i++){
            tr = (TableRow) list.getChildAt(i);
            maNV = (TextView) tr.getChildAt(0);
            if( maNV.getText().toString().trim().equalsIgnoreCase(focusMaNV.getText().toString().trim()+""))
                return i;
        }
        return -1;
    }

    public VanPhongPham findVPPinListVPP ( String maVPP ){
        for( VanPhongPham vpp : vanphongpham_list){
            if( vpp.getMaVpp().trim().equalsIgnoreCase( maVPP ))
                return vpp;
        }
        return null;
    }

    // Hàm này giúp hàm trên bằng cách dẫn tới những dữ liệu có thể cụ thể hóa dữ liệu của hàm trên
    public void setEventTableRowsHelper( TableLayout sublist) {
        // Kiểm tra focus MaNv
        if( focusMaNV == null || focusMaNV.getText().toString().trim().equalsIgnoreCase("")
                || sublist.getChildCount() == 0 )
        {
            Toast.makeText(CapphatVPPLayout.this, "Sorry can't help with no input data",Toast.LENGTH_LONG);
            return ;
        }

        // Rect là 1 rect tàng hình
        int index = findMaNVinTableNV(sublist);
        TableRow tr = (TableRow) sublist.getChildAt( index );
        Log.d("focus",index+"");
        Rect rc = new Rect(0, 0, tr.getWidth(), tr.getHeight());
        // Khi gọi tới thằng TableRow sẽ vẽ 1 Rectangle tàng hình ở thằng TableRow đang chỉ định
        tr.getDrawingRect( rc );
        tr.requestRectangleOnScreen( rc );
        tr.setBackgroundColor(getResources().getColor(R.color.selectedColor));
        // Reset background white for others
        for (int i = 1; i < cp_tablenv_list.getChildCount(); i++) {
            TableRow row = (TableRow) cp_tablenv_list.getChildAt((int) i);
            if (index != (int) row.getId())
                row.setBackgroundColor(getResources().getColor(R.color.white));
        }
    }

    public void setDataImageView(ImageView imageView, byte[] imageBytes){
        if (imageBytes != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            imageView.setImageBitmap(bitmap);
        }
    }

    public void setEventDisplayVPP( String maVPP ){
        // 1. Gọi VPP _ Database để trả về List thông tin // 2. Dựa trên list đó để dò maVPP sau đó get 1 hàng trong đó
        labelVPP.setVisibility(View.VISIBLE);
            VanPhongPham vpp = findVPPinListVPP( maVPP );
            if(vpp == null) return;
            String label = vpp.getMaVpp() + ":    " + vpp.getTenVpp();
            labelVPP.setText(label);
        // 1. Có VanPhongPham rồi thì set on click // 2. Gọi Dialog để xem
        previewVPPBtn.setVisibility(View.VISIBLE);
        previewVPPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Image from Database is handled to load here
                createDialog(R.layout.popup_vpp_previewimage);
                // Custom set Control
                VPP_IP_maVPP = dialog.findViewById(R.id.VPP_IP_maVPP);
                VPP_IP_tenVPP = dialog.findViewById(R.id.VPP_IP_tenVPP);
                VPP_IP_DVT = dialog.findViewById(R.id.VPP_IP_DVT);
                VPP_IP_Gia = dialog.findViewById(R.id.VPP_IP_Gia);
                VPP_IP_Hinh = dialog.findViewById(R.id.VPP_IP_Hinh);
                // Load Data
                setDataImageView( VPP_IP_Hinh, vpp.getHinh() );
                VPP_IP_maVPP.setText( vpp.getMaVpp().toString().trim());
                VPP_IP_tenVPP.setText( vpp.getTenVpp().toString().trim());
                VPP_IP_DVT.setText( vpp.getDvt().toString().trim());
                VPP_IP_Gia.setText( vpp.getGiaNhap().toString().trim());
            }
        });

    }


    // DIALOG HELPER ----------------------------------------------------------------------------
    public void createDialog(int layout) {
        dialog = new Dialog(CapphatVPPLayout.this);
        dialog.setContentView(layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void setControlDialog( ){
        // 1 Form của CP
    }
    public void setEventDialog( ){
        // Them/Xoa/Sua CP
    }

    // LAYOUT 01 -----------------------------------------------
    // Văn phòng phẩm khi init thì select theo thằng CP, sau đó focus vào thằng đầu tiên của VPP
    public void setEventTableVPP(){

    }
    // khi 1 hàng văn phòng phẩm được focus thì mới có nhân viên
    // --------------- CUSTOM HELPER --------------------------------------------------------------------
    public int DPtoPix(int dps) {
        return (int) (dps * scale + 0.5f);
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

    // This Custom Columns' Max Width : 80 / 300
//    public TableRow createRow(Context context, NhanVien nv) {
//        TableRow tr = new TableRow(context);
//        // Id
//
//        //   Ma PB
//        TextView maPB = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
//        // Cần cái này để khi mà maPB đạt tới max width thì nó sẽ tăng height cho bên tenPB luôn
//        // Lưu ý!! : khi đặt LayoutParams thì phải theo thằng cố nội và phải có weight
//        maPB.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
//        maPB.setMaxWidth(DPtoPix(80));
//        maPB.setText(pb.getMapb());
//
//        //   Ten PB
//        TextView tenPB = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
//        // Cần cái này để khi mà tenPB đạt tới max width thì nó sẽ tăng height cho bên maPB luôn
//        tenPB.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
//        tenPB.setText(pb.getTenpb());
//        tenPB.setMaxWidth(DPtoPix(300));
//
//        tr.setBackgroundColor(getResources().getColor(R.color.white));
//        // Add 2 thứ vào row
//        tr.addView(maPB);
//        tr.addView(tenPB);
//
//        return tr;
//    }
    // Table 1
    //    <!-- 85 / 180 p0 / 50 p0 / 80  -->

    // Table 2
    // <!-- 80 / 230 / 90-->


    // Table 3
    // <!-- 80 / 150 / 60 / 60 / 60 -->
    public TableRow createRow(Context context, CapPhat cp) {
    TableRow tr = new TableRow(context);

    //  So phieu
    TextView soPhieu = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
    // Cần cái này để khi mà maVpp đạt tới max width thì nó sẽ tăng height cho bên maNV luôn
    // Lưu ý!! : khi đặt LayoutParams thì phải theo thằng cố nội và phải có weight
    soPhieu.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
    soPhieu.setMaxWidth(DPtoPix(80));
    soPhieu.setText(cp.getSoPhieu());

    //   Ngay cap
    TextView ngayCap = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
    // Cần cái này để khi mà maNV đạt tới max width thì nó sẽ tăng height cho bên maVpp luôn
    ngayCap.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
    ngayCap.setText(formatDate(cp.getNgayCap(), false));
    ngayCap.setMaxWidth(DPtoPix(150));

    //  VPP
    TextView maVpp = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
    // Cần cái này để khi mà maVpp đạt tới max width thì nó sẽ tăng height cho bên maNV luôn
    // Lưu ý!! : khi đặt LayoutParams thì phải theo thằng cố nội và phải có weight
    maVpp.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
    maVpp.setMaxWidth(DPtoPix(60));
    maVpp.setText(cp.getMaVpp());

    //   NV
    TextView maNV = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
    // Cần cái này để khi mà maNV đạt tới max width thì nó sẽ tăng height cho bên maVpp luôn
    maNV.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
    maNV.setText(cp.getMaNv());
    maNV.setMaxWidth(DPtoPix(60));

    //   SL
    TextView soLuong = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
    // Cần cái này để khi mà maNV đạt tới max width thì nó sẽ tăng height cho bên maVpp luôn
    soLuong.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
    soLuong.setText(cp.getSl() + "");
    soLuong.setMaxWidth(DPtoPix(60));

    tr.setBackgroundColor(getResources().getColor(R.color.white));
    // Add 2 thứ vào row
    tr.addView(soPhieu);
    tr.addView(ngayCap);
    tr.addView(maVpp);
    tr.addView(maNV);
    tr.addView(soLuong);

    return tr;
}

    // Table 4
    // <!-- 80 / 300 -->
    public TableRow createRow(Context context, NhanVien nv) {
    TableRow tr = new TableRow(context);
    // Id

    //   Ma PB
    TextView maNV = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
    // Cần cái này để khi mà maNV đạt tới max width thì nó sẽ tăng height cho bên tenNV luôn
    // Lưu ý!! : khi đặt LayoutParams thì phải theo thằng cố nội và phải có weight
    maNV.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
    maNV.setMaxWidth(DPtoPix(80));
    maNV.setText(nv.getMaNv());

    //   Ten PB
    TextView tenNV = (TextView) getLayoutInflater().inflate(R.layout.tvtemplate, null);
    // Cần cái này để khi mà tenNV đạt tới max width thì nó sẽ tăng height cho bên maNV luôn
    tenNV.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT, 10.0f));
    tenNV.setText(nv.getHoTen());
    tenNV.setMaxWidth(DPtoPix(300));

    tr.setBackgroundColor(getResources().getColor(R.color.white));
    // Add 2 thứ vào row
    tr.addView(maNV);
    tr.addView(tenNV);

    return tr;
}

}