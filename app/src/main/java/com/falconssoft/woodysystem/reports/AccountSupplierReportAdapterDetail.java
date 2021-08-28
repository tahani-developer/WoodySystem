package com.falconssoft.woodysystem.reports;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.NewRowInfo;
import com.falconssoft.woodysystem.models.PaymentAccountSupplier;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AccountSupplierReportAdapterDetail extends BaseAdapter {

    private SupplierAccountReport context;
    //    private List<BundleInfo> mOriginalValues;
    private static List<PaymentAccountSupplier> itemsList;
//    private static List<BundleInfo> selectedBundles ;

    List<PaymentAccountSupplier> listOfEmail=new ArrayList<>();
    ProgressDialog proTTn;
    NewRowInfo newRowInfoPic=null;

    public AccountSupplierReportAdapterDetail(SupplierAccountReport context, List<PaymentAccountSupplier> itemsList) {
        this.context = context;
        this.itemsList = itemsList;
    }

    public AccountSupplierReportAdapterDetail() {

    }

    public void setItemsList(List<PaymentAccountSupplier> itemsList) {
        this.itemsList = itemsList;
    }

    @Override
    public int getCount() {
        return itemsList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder {

        TextView text1,text2,text3,text4,text5,text6,text7,text8,text9,text10,pdf,excel;
        ImageView sendEmail;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final ViewHolder holder = new ViewHolder();
        view = View.inflate(context, R.layout.supplier_report_detail_raw, null);

//        holder.pic = (ImageView) view.findViewById(R.id.pic);
//        holder.netBundle = (TextView) view.findViewById(R.id.net);
        holder.text1=view.findViewById(R.id.text1);
        holder.text2 = view.findViewById(R.id.text2);
        holder.text3 = view.findViewById(R.id.text3);
        holder.text4 = view.findViewById(R.id.text4); //supplier after edit not acceptor
        holder.text5 = view.findViewById(R.id.text5);
        holder.text6 = view.findViewById(R.id.text6);
        holder.text7 = view.findViewById(R.id.text7);
        holder.text8 = view.findViewById(R.id.text8);
        holder.text9 = view.findViewById(R.id.text9);
        //holder.text10 = view.findViewById(R.id.text10);

        holder.text1.setText(itemsList.get(i).getSUPLIER());
        holder.text2 .setText(itemsList.get(i).getSTART_BANK());
        holder.text3.setText(itemsList.get(i).getSTART_CASH());
        holder.text4 .setText(itemsList.get(i).getDEBT());
        holder.text5 .setText(itemsList.get(i).getCASHES());
        holder.text6 .setText(itemsList.get(i).getBANKPAYMENT());
        holder.text7.setText(itemsList.get(i).getCASHPAYMENT());
        holder.text8 .setText(itemsList.get(i).getREMININGBANK());
        holder.text9 .setText(itemsList.get(i).getREMININGCASH());


        holder.pdf = view.findViewById(R.id.pdf);
        holder.excel = view.findViewById(R.id.excel);
        holder.sendEmail = view.findViewById(R.id.sendEmail);

        holder.pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                context.showPdfExport(itemsList.get(i));
            }
        });


        holder.excel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.showExcelExport(itemsList.get(i));

            }
        });



        holder.sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context.sendEmail(itemsList.get(i));

            }
        });


        return view;
    }



    void sendEmail(String toEmil, String subject) {


        File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/SendEmailWood");
        File[] listOfFiles = folder.listFiles();
        Log.e("pathh= ", "" + folder.getPath().toString() + "  " + listOfFiles.length);
        List<String> images = new ArrayList<String>();
        for (int i = 0; i < listOfFiles.length; i++) {
            //if (listOfFiles[i].getName().endsWith(".jpg")) {
            images.add(listOfFiles[i].getPath());
            // }
        }
        String subject2 = "";
        if (!TextUtils.isEmpty(subject)) {
            subject2 = subject+" ";
        } else {
            subject2 = "Quality";
        }


        shareWhatsAppA(folder,2,images);


    }

    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/SendEmailWood/";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String targetPdf = directory_path + fileName;
        File path = new File(targetPdf);

        try {
            FileOutputStream out = new FileOutputStream(path);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void shareWhatsAppA(File pdfFile,int pdfExcel,List<String> filePaths){
        try {

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            Uri uri = Uri.fromFile(pdfFile);
            Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            if (pdfFile.exists()) {
                if (pdfExcel == 1) {
                    sendIntent.setType("application/excel");
                } else if (pdfExcel == 2) {
                    sendIntent.setType("plain/text");//46.185.208.4
                }
              //  sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(String.valueOf(uri)));

                sendIntent.putExtra(Intent.EXTRA_SUBJECT,
                        pdfFile.getName() + " Sharing File...");

                sendIntent.putExtra(Intent.EXTRA_TEXT, pdfFile.getName() + " Sharing File");

                ArrayList<Uri> uris = new ArrayList<Uri>();
                //convert from paths to Android friendly Parcelable Uri's
                for (String file : filePaths)
                {
                    File fileIn = new File(file);
                    Uri u = Uri.fromFile(fileIn);
                    uris.add(u);
                }
                sendIntent.putExtra(Intent.EXTRA_STREAM, uris);

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                context.startActivity(shareIntent);

            }
        }catch (Exception e){
            Log.e("drk;d","dfrtr"+e.toString());
            Toast.makeText(context, "Storage Permission"+e.toString(), Toast.LENGTH_SHORT).show();
        }

      //  deleteTempFolder(pdfFile.getPath());
    }

    void showPasswordDialog(int i) {
      Dialog  passwordDialog = new Dialog(context);
        passwordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        passwordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        passwordDialog.setContentView(R.layout.password_dialog);

        TextInputEditText password = passwordDialog.findViewById(R.id.password_dialog_password);
        TextView done = passwordDialog.findViewById(R.id.password_dialog_done);

        done.setText(context.getResources().getString(R.string.done));

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.getText().toString().equals("3030100")) {
                    passwordDialog.dismiss();

                } else
                    Toast.makeText(context, "Password is not correct!", Toast.LENGTH_SHORT).show();

            }
        });

        passwordDialog.show();
    }

    public void deleteTempFolder(String dir) {
        File myDir = new File(dir);
        if (myDir.isDirectory()) {
            String[] children = myDir.list();
            for (int i = 0; i < children.length; i++) {
                new File(myDir, children[i]).delete();
            }
        }
    }

}
