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
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.DatabaseHandler;
import com.falconssoft.woodysystem.ExportToExcel;
import com.falconssoft.woodysystem.ExportToPDF;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.email.SendMailTask;
import com.falconssoft.woodysystem.models.NewRowInfo;
import com.falconssoft.woodysystem.models.Settings;
import com.falconssoft.woodysystem.stage_one.AddNewAdapter;
import com.falconssoft.woodysystem.stage_one.AddNewRaw;
import com.falconssoft.woodysystem.stage_one.StageOne;
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
import java.util.List;

import static android.provider.CalendarContract.CalendarCache.URI;
import static com.falconssoft.woodysystem.reports.AcceptanceReport.truckNoBeforeUpdate2;

public class AcceptanceReportAdapter extends BaseAdapter {

    private AcceptanceReport context;
    //    private List<BundleInfo> mOriginalValues;
    private static List<NewRowInfo> itemsList;
    private static List<NewRowInfo> bundles;
//    private static List<BundleInfo> selectedBundles ;

    List<NewRowInfo> listOfEmail=new ArrayList<>();
    ProgressDialog proTTn;
    NewRowInfo newRowInfoPic=null;

    public AcceptanceReportAdapter(AcceptanceReport context, List<NewRowInfo> itemsList, List<NewRowInfo> bundles) {
        this.context = context;
        this.itemsList = itemsList;
        this.bundles = bundles;
    }

    public AcceptanceReportAdapter() {

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
        TextView truckNo, acceptor, ttn, netBundle, date, noOfBundles, rejected, cubic, cubicRej, serial,acceptCubic,pdf,excel;
        ImageView edit,sendEmail;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final ViewHolder holder = new ViewHolder();
        view = View.inflate(context, R.layout.acceptance_report_row, null);

//        holder.pic = (ImageView) view.findViewById(R.id.pic);
//        holder.netBundle = (TextView) view.findViewById(R.id.net);
        holder.sendEmail=view.findViewById(R.id.acceptanceReport_email);
        holder.preview = view.findViewById(R.id.preview);
        holder.truckNo = view.findViewById(R.id.truck_no);
        holder.acceptor = view.findViewById(R.id.name_of_person_to_accept); //supplier after edit not acceptor
        holder.date = view.findViewById(R.id.date_of_acceptance);
        holder.ttn = view.findViewById(R.id.ttn_no);
        holder.noOfBundles = view.findViewById(R.id.noOfBundles_acceptance);
        holder.rejected = view.findViewById(R.id.rejected_pieces);
        holder.edit = view.findViewById(R.id.acceptanceReport_edit);
        holder.cubic = view.findViewById(R.id.truck_report_cubic);
        holder.cubicRej = view.findViewById(R.id.truck_report_cubic_rej);
        holder.serial = view.findViewById(R.id.truck_report_serial);
        holder.image = view.findViewById(R.id.truckReport_image);
        holder.acceptCubic=view.findViewById(R.id.truck_report_cubic_accept);
        holder.pdf=view.findViewById(R.id.truck_report_pdf);
        holder.excel=view.findViewById(R.id.truck_report_excel);
        holder.serial.setText(itemsList.get(i).getSerial());
        holder.truckNo.setText(itemsList.get(i).getTruckNo());
        holder.acceptor.setText(findSupplier(itemsList.get(i)));//itemsList.get(i).getAcceptedPersonName());
        holder.date.setText(itemsList.get(i).getDate());
        holder.ttn.setText(itemsList.get(i).getTtnNo());
//        holder.netBundle.setText( itemsList.get(i).getNetBundles());
        holder.noOfBundles.setText("" + itemsList.get(i).getNetBundles());
        holder.rejected.setText(itemsList.get(i).getTotalRejectedNo());
        holder.cubic.setText("" + itemsList.get(i).getCubic());
        holder.cubicRej.setText("" + itemsList.get(i).getCubicRej());
        double accCubic= 0;
        accCubic= itemsList.get(i).getCubic()- itemsList.get(i).getCubicRej();
        accCubic=Double.parseDouble(String.format("%.3f", accCubic));
        holder.acceptCubic.setText(""+ accCubic);

        AcceptanceReport obj = new AcceptanceReport();

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPasswordDialog(i);

            }
        });

        holder.pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new JSONTaskTTN(itemsList.get(i),0).execute();

            }
        });


        holder.excel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               new JSONTaskTTN(itemsList.get(i),2).execute();

            }
        });

        holder.preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                obj.previewLinear(itemsList.get(i).getSerial(), context);

            }
        });

        holder.sendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                obj.previewLinear(itemsList.get(i).getSerial(), context);
                new JSONTaskTTN(itemsList.get(i),1).execute();

            }
        });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context.previewPics2(itemsList.get(i), context);
            }
        });

        return view;
    }

    String findSupplier(NewRowInfo newRowInfo) {
        for (int i = 0; i < bundles.size(); i++) {
            if (newRowInfo.getSerial().equals(bundles.get(i).getSerial()))
                return bundles.get(i).getSupplierName();
        }

        return "----";
    }


    private class JSONTaskTTN extends AsyncTask<String, String, String> {
        Settings generalSettings = new DatabaseHandler(context).getSettings();
        NewRowInfo newRowInfos=null;
        int flag;

        public JSONTaskTTN(NewRowInfo newRowInfos,int flag) {
        this.newRowInfos=newRowInfos;
        this.flag=flag;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            proTTn = new ProgressDialog(context, R.style.MyAlertDialogStyle);
            proTTn.setMessage("Please Waiting...");
            proTTn.show();
        }

        @Override
        protected String doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;
            String finalJson = null;
            try {
                URL url = new URL("http://" + generalSettings.getIpAddress() + "/import.php?FLAG=19&TTN_NO=" + newRowInfos.getTtnNo().trim()+"&SERIAL="+newRowInfos.getSerial());

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                reader = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;
                Log.e("url11*********", url.toString());
                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                finalJson = sb.toString();
                Log.e("finalJson*********", finalJson);


            } catch (IOException e) {
                Log.e("Import Data2", e.getMessage().toString());
            }


            return finalJson;
        }


        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);

//            tableLayout.removeAllViews();
//            headerTableLayout.removeAllViews();
            // rejectAdd();
            if (result != null) {
                if (!result.contains("noBundleFound")) {
                    // Log.e("result", "*****************" + result.size());
                    Gson gson = new Gson();
                    NewRowInfo list = gson.fromJson(result, NewRowInfo.class);
                    listOfEmail.clear();
                    listOfEmail.addAll(list.getDetailsList());

                    // tableLayout.removeAllViews();
                    //  addTableHeader(headerTableLayout);
//                    for (int i = 0; i < listOfEmail.size(); i++) {
//                        supplierName = listOfEmail.get(i).getSupplierName();
//                        tableRow = new TableRow(context);
//                        gradeText = listOfEmail.get(i).getGrade();
//
////                        recyclerViewAdd.setLayoutManager(new LinearLayoutManager(context));
////                        addNewAdapter = new AddNewAdapter(context, listOfEmail);
////                        recyclerViewAdd.setAdapter(addNewAdapter);
//                    }
                    new BitmapImage3(newRowInfos,flag).execute(listOfEmail.get(0));
                    // rejectAdd();
                } else {
                    Toast.makeText(context, "The TTN.NO Not Found", Toast.LENGTH_SHORT).show();
                    proTTn.dismiss();
                }


            } else {
                proTTn.dismiss();
                Toast.makeText(context, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class BitmapImage3 extends AsyncTask<NewRowInfo, String, NewRowInfo> {
          Settings generalSettings = new DatabaseHandler(context).getSettings();
        NewRowInfo newRowInfos=null;
        int flag;

        public BitmapImage3(NewRowInfo newRowInfos,int flag) {
            this.newRowInfos =newRowInfos;
        this.flag=flag;
        }

        @Override
        protected NewRowInfo doInBackground(NewRowInfo... pictures) {

            newRowInfoPic = pictures[0];
            URL url;
            Bitmap bitmap;
            try {
                if (!newRowInfoPic.equals("null")) {
                    for (int i = 0; i < 15; i++) {

                        switch (i) {
                            case 0:
                                if (!pictures[0].getImageOne().equals("null")) {//http://192.168.2.17:8088/woody/images/2342_img_1.png
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageOne());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic11(bitmap);
                                        newRowInfoPic.setImageOne(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic11(bitmap);

                                    } catch (Exception e) {
//                                        pictures[0].setPic11(bitmap);
                                    }
                                }
                                break;
                            case 1:
                                if (!pictures[0].getImageTwo().equals("null")) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageTwo());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic22(bitmap);
                                        newRowInfoPic.setImageTwo(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic22(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic22(bitmap);
                                    }
                                }
                                break;
                            case 2:
                                if (!pictures[0].getImageThree().equals("null")) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageThree());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic33(bitmap);
                                        newRowInfoPic.setImageThree(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic33(bitmap);

                                    } catch (Exception e) {
//                                        pictures[0].setPic33(bitmap);
                                    }
                                }
                                break;
                            case 3:
                                if (!pictures[0].getImageFour().equals("null")) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageFour());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic44(bitmap);
                                        newRowInfoPic.setImageFour(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic44(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic44(bitmap);
                                    }
                                }
                                break;
                            case 4:
                                if (!pictures[0].getImageFive().equals("null")) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageFive());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic55(bitmap);
                                        newRowInfoPic.setImageFive(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic55(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic55(bitmap);
                                    }
                                }
                                break;
                            case 5:
                                if (!pictures[0].getImageSix().equals("null")) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageSix());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic66(bitmap);
                                        newRowInfoPic.setImageSix(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic66(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic66(bitmap);
                                    }
                                }
                                break;
                            case 6:
                                if (!pictures[0].getImageSeven().equals("null")) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageSeven());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic77(bitmap);
                                        newRowInfoPic.setImageSeven(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic77(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic77(bitmap);
                                    }
                                }
                                break;
                            case 7:
                                if (!pictures[0].getImageEight().equals("null")) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageEight());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic88(bitmap);
                                        newRowInfoPic.setImageEight(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic88(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;

                            case 8:
                                if (!pictures[0].getImage9().equals("null")) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImage9());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic99(bitmap);
                                        newRowInfoPic.setImage9(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic99(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;


                            case 9:
                                if (!pictures[0].getImage10().equals("null")) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImage10());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic1010(bitmap);
                                        newRowInfoPic.setImage10(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic1010(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;

                            case 10:
                                if (!pictures[0].getImage11().equals("null")) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImage11());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic1111(bitmap);
                                        newRowInfoPic.setImage11(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic1111(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;

                            case 11:
                                if (!pictures[0].getImage12().equals("null")) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImage12());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic1212(bitmap);
                                        newRowInfoPic.setImage12(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic1212(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;

                            case 12:
                                if (!pictures[0].getImage13().equals("null")) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImage13());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic1313(bitmap);
                                        newRowInfoPic.setImage13(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic1313(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;

                            case 13:
                                if (!pictures[0].getImage14().equals("null")) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImage14());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic1414(bitmap);
                                        newRowInfoPic.setImage14(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic1414(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;

                            case 14:

                                if (!pictures[0].getImage15().equals("null")) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImage15());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic1515(bitmap);
                                        newRowInfoPic.setImage15(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic1515(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("fromclass2", "exception:doInBackground " + e.getMessage());
                return null;
            }
            return newRowInfoPic;// BitmapFactory.decodeStream(in);
        }

        @Override
        protected void onPostExecute(NewRowInfo pictures) {


            if (pictures != null) {
                //fillImageBitmap(newRowInfoPic);
               // fillImagesFromEmail();
                proTTn.dismiss();

            } else {
                proTTn.dismiss();
                Toast.makeText(context, "Fail get Pic", Toast.LENGTH_SHORT).show();
            }

            if (listOfEmail.size() != 0) {

                try {
                    File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/SendEmailWood");
                    deleteTempFolder(folder.getPath());
                }catch (Exception e){
                    Log.e("Delete Folder ","folder");
                }

                double accCubic= 0;
                accCubic=newRowInfos.getCubic()- newRowInfos.getCubicRej();
                accCubic=Double.parseDouble(String.format("%.3f", accCubic));
                ExportToPDF obj = new ExportToPDF(context);

                if(flag==0) {
                    obj.exportTruckAcceptance(listOfEmail, newRowInfos, "", "" + newRowInfos.getCubic(), "" + newRowInfos.getNoOfRejected(),"" + newRowInfos.getCubicRej(), "" + accCubic);
                }else   if(flag==1) {
                    obj.exportTruckAcceptanceSendEmail(listOfEmail, "", "" + newRowInfos.getCubic()
                            , "" + newRowInfos.getNoOfRejected(), "" + newRowInfos.getCubicRej(), "" + accCubic);
                    if (newRowInfoPic != null) {
                        fillImageBitmap(newRowInfoPic);
                    }
                    sendEmail("", "");
                    //sendEmailDialog();
                }else   if(flag==2) {
                    ExportToExcel.getInstance().createExcelFile(context, "Acceptance_Report_2.xls", 8, listOfEmail, null);

                }
            } else {
                Toast.makeText(context, "no Data ", Toast.LENGTH_SHORT).show();
            }


        }
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
                    truckNoBeforeUpdate2 = itemsList.get(i).getTruckNo();
                    context.goToEditPage(itemsList.get(i));
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
    public void sendEmail(String subject,String email ,String message) {
        try {
//            email = etEmail.getText().toString();
//            subject = etSubject.getText().toString();
//            message = etMessage.getText().toString();
            final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("plain/text");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
            if (URI != null) {
                emailIntent.putExtra(Intent.EXTRA_STREAM, URI);
            }
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
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
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
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

}
