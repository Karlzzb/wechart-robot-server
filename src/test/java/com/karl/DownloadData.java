package com.karl;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
/**
 *
 * @author wing
 */
public final class DownloadData {
       private final SimpleStringProperty fileName = new SimpleStringProperty();
       private final SimpleStringProperty status = new SimpleStringProperty();
       private final SimpleStringProperty dlSpeed = new SimpleStringProperty();
       private final SimpleDoubleProperty progress = new SimpleDoubleProperty();
       private final SimpleStringProperty downloadSize = new SimpleStringProperty();      
       private final SimpleStringProperty dlPercent = new SimpleStringProperty();    
       private String uuid;

         public DownloadData(String filename, double progress) {
           setFileName(filename);
           setProgress(progress);
       }     

       public DownloadData(String status, String filename, String dlSpeed, double progress) {
           setStatus(status);
           setFileName(filename);
           setDlSpeed(dlSpeed);
           setProgress(progress);
       }
    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName.get();
    }
    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public SimpleStringProperty fileNameProperty(){
        return fileName;
    }
    /**
     * @return the status
     */
    public String getStatus() {
        return status.get();
    }
    /**
     * @param status the statusto set
     */
    public void setStatus(String status) {
        this.status.set(status);
    }

   public SimpleStringProperty statusProperty(){
        return status;
    }
    /**
     * @return the String
     */
    public String getDlSpeed() {
        return dlSpeed.get();
    }
    /**
     * @param dlSpeed the dlSpeed to set
     */
    public void setDlSpeed(String dlSpeed) {
        this.dlSpeed.set(dlSpeed);
    }
    public SimpleStringProperty dlSpeedProperty(){
        return dlSpeed;
    }

    /**
     * @return the progress
     */
    public double getProgress() {
        return progress.get();
    }
    /**
     * @param progress the progress to set
     */
    public void setProgress(double progress) {
        this.progress.set(progress);
    }

    public SimpleDoubleProperty progressProperty(){
        return progress;
    }   

    public String getDownloadSize() {
        return downloadSize.get();
    }
    public void setDownloadSize(String downloadSize) {
        this.downloadSize.set(downloadSize);
    }
    public SimpleStringProperty downloadSizeProperty(){
        return downloadSize;
    }

    public String getDlPercent() {
        return dlPercent.get();
    }
    public void setDlPercent(String dlPercent) {
        this.dlPercent.set(dlPercent);
    }
    public SimpleStringProperty dlPercentProperty(){
        return dlPercent;
    }

    public String getUUID() {
        return uuid;
    }
    public void setUUID(String uuid) {
        this.uuid = uuid;
    }  
}
