package com.uteq.uteqdevelopers.noticiasuteq.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by JOMA on 25/08/2017.
 */
public class SaveImage {

        private Context TheThis;
        private String NameOfFolder = "/UTEQ";
        private String NameOfFile = "IMG-UTEQ-";

        public void SaveImage(final Context context, final Bitmap ImageToSave) {

            AlertDialog.Builder dConfirmacionD= new AlertDialog.Builder(context);
            dConfirmacionD.setTitle("Descargar");
            dConfirmacionD.setMessage(Constants.MSG_CONFIRMAR_GUARDAR_IMAGEN);
            dConfirmacionD.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    TheThis = context;
                    String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + NameOfFolder;
                    String CurrentDateAndTime = getCurrentDateAndTime();
                    File dir = new File(file_path);

                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    File file = new File(dir, NameOfFile + CurrentDateAndTime + ".jpg");

                    try {
                        FileOutputStream fOut = new FileOutputStream(file);

                        ImageToSave.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                        fOut.flush();
                        fOut.close();
                        MakeSureFileWasCreatedThenMakeAvabile(file);
                        AbleToSave();
                    }

                    catch(FileNotFoundException e) {
                        UnableToSave();
                    }
                    catch(IOException e) {
                        UnableToSave();
                    }
                    dialog.dismiss();
                }
            });
            dConfirmacionD.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dConfirmacionD.show();


        }

        private void MakeSureFileWasCreatedThenMakeAvabile(File file){
            MediaScannerConnection.scanFile(TheThis,
                    new String[] { file.toString() } , null,
                    new MediaScannerConnection.OnScanCompletedListener() {

                        public void onScanCompleted(String path, Uri uri) {
                        }
                    });
        }

        private String getCurrentDateAndTime() {
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-­ss");
            String formattedDate = df.format(c.getTime());
            return formattedDate.replaceAll("-","");
        }

        private void UnableToSave() {
            Toast.makeText(TheThis, "¡Conceda los permisos necesarios para guardar!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", TheThis.getPackageName(), null);
            intent.setData(uri);
            TheThis.startActivity(intent);
        }

        private void AbleToSave() {
            Toast.makeText(TheThis, "Imagen guardada en la galería.", Toast.LENGTH_SHORT).show();
        }

}
