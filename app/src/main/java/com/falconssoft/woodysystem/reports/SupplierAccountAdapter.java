package com.falconssoft.woodysystem.reports;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.TextInputEditText;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.stage_one.AccountSupplier;
import com.falconssoft.woodysystem.DatabaseHandler;
import com.falconssoft.woodysystem.ExportToExcel;
import com.falconssoft.woodysystem.ExportToPDF;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.NewRowInfo;
import com.falconssoft.woodysystem.models.Settings;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.provider.CalendarContract.CalendarCache.URI;

public class SupplierAccountAdapter extends BaseAdapter {

    private AccountSupplier context;
    private static List<NewRowInfo> itemsList;

    List<NewRowInfo> listOfEmail=new ArrayList<>();
    ProgressDialog proTTn;
    NewRowInfo newRowInfoPic=null;

    Dialog passwordDialog;
    public SupplierAccountAdapter(AccountSupplier context, List<NewRowInfo> itemsList) {
        this.context = context;
        this.itemsList = itemsList;
    }

    public SupplierAccountAdapter() {

    }

    public void setItemsList(List<NewRowInfo> itemsList) {
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
        ImageView pic, image;
        Button preview;
        TextView cash_,debt,cash,prices,supplier,truckNo, ttn, Thic,width,length,date_of_acceptance,piecesA,TruckCBM,piecesR,rejCbm,AcceptCbm,grade,noOfBundle,pdf,excel;
        ImageView edit,sendEmail;
        CheckBox PriceCashCheckBox;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final ViewHolder holder = new ViewHolder();
        view = View.inflate(context, R.layout.supplier_account_raw, null);

//        holder.pic = (ImageView) view.findViewById(R.id.pic);
//        holder.netBundle = (TextView) view.findViewById(R.id.net);
        holder.supplier=view.findViewById(R.id.supplier);
        holder.sendEmail=view.findViewById(R.id.acceptanceReport_email);
        holder.truckNo = view.findViewById(R.id.truck_no);
        holder.Thic = view.findViewById(R.id.thic); //supplier after edit not acceptor
        holder.width = view.findViewById(R.id.width);
        holder.ttn = view.findViewById(R.id.ttn_no);
        holder.length = view.findViewById(R.id.length);
        holder.noOfBundle = view.findViewById(R.id.bundel_no);
        holder.date_of_acceptance = view.findViewById(R.id.date);
        holder.piecesA = view.findViewById(R.id.pieces);
        holder.TruckCBM = view.findViewById(R.id.truck_accept);
        holder.prices = view.findViewById(R.id.prices);
      //  holder.rejCbm = view.findViewById(R.id.rejCbm);
       holder.piecesR=view.findViewById(R.id.rej_no);
        holder.pdf=view.findViewById(R.id.truck_report_pdf);
        holder.excel=view.findViewById(R.id.truck_report_excel);
       // holder.grade=view.findViewById(R.id.grade);
        holder.cash=view.findViewById(R.id.cash);
        holder.debt=view.findViewById(R.id.debt);
        holder.cash_=view.findViewById(R.id.cash_);
        holder.PriceCashCheckBox=view.findViewById(R.id.PCCheckBox);
      //  holder.payment=view.findViewById(R.id.payment);

        holder.truckNo.setText(itemsList.get(i).getTruckNo());
        holder.ttn.setText(itemsList.get(i).getTtnNo());
        holder.noOfBundle.setText("" + itemsList.get(i).getNoOfBundles());
        holder.piecesR.setText(itemsList.get(i).getTotalRejectedNo());
       // holder.TruckCBM.setText("" + itemsList.get(i).getTruckCMB());
        holder.supplier.setText(itemsList.get(i).getSupplierName());
        //holder.rejCbm.setText("" + itemsList.get(i).getCbmRej());

       // holder.grade.setText(itemsList.get(i).getGrade());
        double accCubic= 0;
        accCubic=Double.parseDouble( convertToEnglish(itemsList.get(i).getTruckCMB()))- Double.parseDouble(convertToEnglish(itemsList.get(i).getCbmRej()));

        double acc=Double.parseDouble(convertToEnglish(""+accCubic));
        Log.e("Doubleee",""+convertToEnglish(""+acc)+"    "+acc +"   "+ accCubic);

         holder.TruckCBM.setText(""+ itemsList.get(i).getCbmAccept());


        holder.Thic.setText(""+itemsList.get(i).getThickness());
        holder.width.setText(""+itemsList.get(i).getWidth());
        holder.length.setText(""+itemsList.get(i).getLength());
        holder.date_of_acceptance.setText(""+itemsList.get(i).getDate());
        holder.piecesA.setText(""+itemsList.get(i).getNoOfPieces());
        try {
            holder.prices.setText("" + String.format("%.3f", itemsList.get(i).getPrice()));
            double debt=0.0;
         //   debt=itemsList.get(i).getPrice()*Double.parseDouble(itemsList.get(i).getTruckCMB());
            debt=itemsList.get(i).getPrice()*acc;

            holder.debt.setText(""+String.format("%.3f", debt));
        }catch (Exception e){
            holder.prices.setText("0.0" );
            holder.debt.setText("0.0");
        }
        try {
            holder.cash.setText("" + String.format("%.3f", itemsList.get(i).getCash()));
            double cashes=0.0;
           // cashes=itemsList.get(i).getCash()*Double.parseDouble(itemsList.get(i).getTruckCMB());
             cashes=itemsList.get(i).getCash()*acc;
            holder.cash_.setText(""+String.format("%.3f", cashes));
        }catch (Exception e){
            holder.cash_.setText("0.0" );
            holder.cash.setText("0.0");
        }

        holder.PriceCashCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.PriceCashCheckBox.isChecked()) {
                    context.checkedValueInMainList(i,true);
                }else {
                    context.checkedValueInMainList(i,false);

                }
            }
        });

     //   context.Acc(i);

//
//        holder.payment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // context.exportToPdf(itemsList.get(i));
//
//               paymentDialog();
//
//            }
//        });

        if(itemsList.get(i).isCh()){
            holder.PriceCashCheckBox.setChecked(true);
        }else {
            holder.PriceCashCheckBox.setChecked(false);
        }


        holder.prices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // context.exportToPdf(itemsList.get(i));
                //showPasswordDialog(i,1);

                priceDialog(i,1);


            }
        });


        holder.cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // context.exportToPdf(itemsList.get(i));
//showPasswordDialog(i,2);
                priceDialog(i,2);
            }
        });

        holder.pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
          context.showPdfExport(itemsList.get(i));

            }
        });


        holder.excel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              //  priceDialog(i,2);

                context.showExcelExport(itemsList.get(i));

            }
        });



        holder.sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/SendEmailWood");
                    context.deleteTempFolder(folder.getPath());
                }catch (Exception e){
                    Log.e("Delete Folder ","folder");
                }

          ExportToPDF obj = new ExportToPDF(context);
                    obj.exportSupplierAccountSupplierEmail(Collections.singletonList(itemsList.get(i)),itemsList.get(i).getSupplierName());

                    sendEmail("","");
            }
        });



        return view;
    }

    private void paymentDialog() {
        final Dialog dialog = new Dialog(context, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.payment_dialog);
        EditText priceText, Date,value,invNo,payer;
        RadioButton bankPay,cashPay;
        Button doneButton=dialog.findViewById(R.id.doneButton);
         priceText=dialog.findViewById(R.id.new_price);
        Date=dialog.findViewById(R.id.Date);
        value=dialog.findViewById(R.id.value);
        invNo=dialog.findViewById(R.id.invNo);
        payer=dialog.findViewById(R.id.payer);
        bankPay=dialog.findViewById(R.id.bankPay);
        cashPay=dialog.findViewById(R.id.cashPay);



        dialog.show();

    }


    public String bitMapToString(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] arr = baos.toByteArray();
            String result = Base64.encodeToString(arr, Base64.DEFAULT);
            return result;
        }

        return "";
    }

    void showPasswordDialog(int index ,int flag) {
        passwordDialog = new Dialog(context);
        passwordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        passwordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        passwordDialog.setContentView(R.layout.password_dialog);

        TextInputEditText password = passwordDialog.findViewById(R.id.password_dialog_password);
        TextView done = passwordDialog.findViewById(R.id.password_dialog_done);

        done.setText(context.getResources().getString(R.string.done));

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.getText().toString().equals("3030300")) {
                    passwordDialog.dismiss();
                    switch (flag){
                        case 1:
                            priceDialog(index,1);
                            break;
                        case 2:
                            priceDialog(index,2);

                            break;
                    }
                } else
                    Toast.makeText(context, "Password is not correct!", Toast.LENGTH_SHORT).show();

            }
        });

        passwordDialog.show();
    }
    void priceDialog(int index,int flag){
        final Dialog dialog = new Dialog(context, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.price_editing_dialog);

        Button doneButton=dialog.findViewById(R.id.doneButton);
        EditText priceText=dialog.findViewById(R.id.new_price);


        if(flag==1) {
            priceText.setText("" + itemsList.get(index).getPrice());
        }else if (flag==2){
            priceText.setText("" + itemsList.get(index).getCash());
        }
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!priceText.getText().toString().equals("")&&!priceText.getText().toString().equals(".")) {
                   // itemsList.get(index).setPrice(Double.parseDouble(priceText.getText().toString()));
                    context.notiList(index,Double.parseDouble(priceText.getText().toString()),flag);
                    dialog.dismiss();
                }else{
                    priceText.setError("Required !");
                }


            }
        });

        dialog.show();
    }


    void sendEmailDialog() {
        final Dialog dialog = new Dialog(context, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.send_email_dialog);
        Button sendButton = dialog.findViewById(R.id.sendButton);
        EditText toEmail = dialog.findViewById(R.id.addNewRaw_toEmail);
        EditText subject = dialog.findViewById(R.id.addNewRaw_subject);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!toEmail.getText().toString().equals("")) {
                    sendEmail(toEmail.getText().toString(), subject.getText().toString());
                    dialog.dismiss();
                } else {
                    toEmail.setError("Required!");
                }
            }
        });


        dialog.show();
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


//        new SendMailTask(context,folder.getPath(),0).execute("quality@blackseawood.com",
//                "12345678Q",
//                toEmil,
//                "quality BLACK SEA WOOD",
//                subject2,
//                images
//        );

//
//        BackgroundMail.newBuilder(AddNewRaw.this)//rawanwork2021@gmail.com
//                .withUsername("quality@blackseawood.com")//quality@blackseawood.com
//                .withPassword("12345678Q")
//                .withMailto(toEmil)
//                .withType(BackgroundMail.TYPE_PLAIN)
//                .withSubject("quality BLACK SEA WOOD")
//                .withBody(subject2 /*"this is the body \n www.google.com  \n  http://5.189.130.98:8085/import.php?FLAG=3 \n "  */)
//                .withProcessVisibility(true)
//                .withAttachments(images)
//                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
//                    @Override
//                    public void onSuccess() {
//                        //do some magic
//                        clear();
//                        deleteTempFolder(folder.getPath());
//                    }
//                })
//                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
//                    @Override
//                    public void onFail() {
//                        //do some magic
//                    }
//                })
//                .send();


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

    private void fillImageBitmap(NewRowInfo newRowInfo) {

        if (!newRowInfo.getImageOne().equals("null")) {
            createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImageOne()), "image_1.png");
        }
        if (!newRowInfo.getImageTwo().equals("null")) {
            createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImageTwo()), "image_2.png");
        }
        if (!newRowInfo.getImageThree().equals("null")) {
            createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImageThree()), "image_3.png");
        }
        if (!newRowInfo.getImageFour().equals("null")) {
            createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImageFour()), "image_4.png");
        }
        if (!newRowInfo.getImageFive() .equals("null")) {
            createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImageFive()), "image_5.png");
        }
        if (!newRowInfo.getImageSix().equals("null")) {
            createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImageSix()), "image_6.png");
        }
        if (!newRowInfo.getImageSeven() .equals("null")) {
            createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImageSeven()), "image_7.png");
        }
        if (!newRowInfo.getImageEight().equals("null")) {
            createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImageEight()), "image_8.png");
        }
        if (!newRowInfo.getImage9().equals("null")) {
            createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImage9()), "image_9.png");
        }
        if (!newRowInfo.getImage10().equals("null")) {
            createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImage10()), "image_10.png");
        }
        if (!newRowInfo.getImage11().equals("null")) {
            createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImage11()), "image_11.png");
        }
        if (!newRowInfo.getImage12() .equals("null")) {
            createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImage12()), "image_12.png");
        }
        if (!newRowInfo.getImage13().equals("null")) {
            createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImage13()), "image_13.png");
        }
        if (!newRowInfo.getImage14().equals("null")) {
            createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImage14()), "image_14.png");
        }
        if (!newRowInfo.getImage15().equals("null")) {
            createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImage15()), "image_15.png");
        }

    }

    public Bitmap stringToBitMap(String image) {
        try {
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
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

    }


    public void sendEmail(String subject,String email ,String message) {
        try {
//            email = etEmail.getText().toString();
//            subject = etSubject.getText().toString();
//            message = etMessage.getText().toString();
            final Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            if (URI != null) {
                emailIntent.putExtra(Intent.EXTRA_STREAM, URI);
            }
            emailIntent.putExtra(Intent.EXTRA_TEXT, message);
            context.startActivity(Intent.createChooser(emailIntent, "Sending email..."));
        } catch (Throwable t) {
            Toast.makeText(context, "Request failed try again: "+ t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public  void email( String emailTo, String emailCC,
                             String subject, String emailText, List<String> filePaths)
    {
        //need to "send multiple" to get more than one attachment
        final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL,
                new String[]{emailTo});
//        emailIntent.putExtra(android.content.Intent.EXTRA_CC,
//                new String[]{emailCC});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, emailText);
        //has to be an ArrayList
        ArrayList<Uri> uris = new ArrayList<Uri>();
        //convert from paths to Android friendly Parcelable Uri's
        for (String file : filePaths)
        {
            File fileIn = new File(file);
            Uri u = Uri.fromFile(fileIn);
            uris.add(u);
        }
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
//        context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        Intent shareIntent = Intent.createChooser(emailIntent,  "Send mail...");
        context.startActivity(shareIntent);
    }

    public static void sendEmailMulipleFiles(Context context, String toAddress, String subject, String body, ArrayList<String> attachmentPath) throws Exception {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] { toAddress });
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, body);
            intent.setType("message/rfc822");
            PackageManager pm = context.getPackageManager();
            List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
            ResolveInfo best = null;
            for (final ResolveInfo info : matches) {
                if (info.activityInfo.packageName.contains(".gm.") || info.activityInfo.name.toLowerCase().contains("gmail"))
                    best = info;
            }
            ArrayList<Uri> uri = new ArrayList<Uri>();
            for (int i = 0; i < attachmentPath.size(); i++) {
                File file = new File(attachmentPath.get(i));
                uri.add(Uri.fromFile(file));
            }

            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uri);


            if (best != null)
                intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);

            context.startActivity(Intent.createChooser(intent, "Choose an email application..."));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
    public String convertToEnglish(String value) {
        String newValue = (((((((((((value + "").replaceAll("١", "1")).replaceAll("٢", "2")).replaceAll("٣", "3")).replaceAll("٤", "4")).replaceAll("٥", "5")).replaceAll("٦", "6")).replaceAll("٧", "7")).replaceAll("٨", "8")).replaceAll("٩", "9")).replaceAll("٠", "0").replaceAll("٫", "."));
        return newValue;
    }
}
