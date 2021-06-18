package com.example.giuaki.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.giuaki.Api.VanPhongPham;
import com.example.giuaki.R;
import com.example.giuaki.WebService;
import com.squareup.picasso.Picasso;

import java.util.List;

public class VPPAdapter2 extends BaseAdapter {
    private List<VanPhongPham> list;
    private Context context;
    LayoutInflater inflater;

    public VPPAdapter2(Context context, List<VanPhongPham> list) {
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    String webPath = "http://"+ WebService.host()+"/ImageController-get?hinh=";
    public void setDataImageView(ImageView imageView, String imageUri){
        if( imageUri == null || imageUri.equalsIgnoreCase("null") || imageUri.equalsIgnoreCase("")) return;
        Log.d("data",webPath+imageUri);
        Picasso.get().load(webPath+imageUri).into(imageView);
    }
    public  String checkStr( String str ){
        if( str == null || str.length() == 0 || str.equalsIgnoreCase("null"))
            return "";
        return str;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        // getLayout
        VanPhongPham vpp = list.get(position);
        if( vpp.getMaVpp().equals("all")){
            view = inflater.inflate(R.layout.simpletemplate, null);
            TextView  tenVPP = view.findViewById(R.id.tenVPP);
            tenVPP.setText("Tất cả văn phòng phẩm");
            return view;
        }
        String id = vpp.getMaVpp();
        String ten = vpp.getTenVpp();
        String dvt = vpp.getDvt();
        String hinh = vpp.getHinh();
        String sl = vpp.getSoLuong();
        String ncc = vpp.getMaNcc();

        view = inflater.inflate(R.layout.vpptemplate2, null);
        // getControl.
        ImageView hinhVPP = view.findViewById(R.id.hinhVPP);
        setDataImageView(hinhVPP, hinh );
        TextView  idVPP   = view.findViewById(R.id.idVPP);
        TextView  tenVPP   = view.findViewById(R.id.tenVPP);
        TextView  dvtVPP   = view.findViewById(R.id.dvtVPP);
        TextView  slVPP   = view.findViewById(R.id.slVPP);
        TextView nccVPP   = view.findViewById(R.id.nccVPP);
        // Fetch data
        idVPP.setText(checkStr(id));
        tenVPP.setText(checkStr(ten));
        dvtVPP.setText(checkStr(dvt));
        slVPP.setText(checkStr(sl));
        nccVPP.setText( String.format("%s đã giao thành công", ncc) );
        // Trả về view kết quả.
        return view;
    }
}
