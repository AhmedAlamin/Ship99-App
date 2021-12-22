package com.Ship99_Official.ship99.Track;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;



public class RequestModel implements Serializable{
    //still not added to firebase automatically
    private String userId;
    private String orderNumber;
    private String ClientAddress;
    private String Name;
    private String NumberOfClient;
    private String PickupAddress;
    private String TotalPrice;
    private String Weight;
    private String nearest_sign_note;
    private String notes_of_shipping;
    private String photoOfProduct;
    private String status;
    private String status_uid;
    private String status_pickupAddress;
    private String date;
//    private String index;



    public RequestModel() {
    }

    public RequestModel( String status_pickupAddress,String date, String userId, String clientAddress, String name, String numberOfClient, String pickupAddress, String totalPrice, String weight, String nearest_sign_note, String notes_of_shipping, String photoOfProduct, String status,String orderNumber,String status_uid) {
        this.userId = userId;
        this.status_pickupAddress = status_pickupAddress;
//        this.index = index;
        this.date = date;
        this.status_uid = status_uid;
        ClientAddress = clientAddress;
        Name = name;
        this.NumberOfClient = numberOfClient;
        PickupAddress = pickupAddress;
        TotalPrice = totalPrice;
        Weight = weight;
        this.nearest_sign_note = nearest_sign_note;
        this.notes_of_shipping = notes_of_shipping;
        this.photoOfProduct = photoOfProduct;
        this.status = status;
        this.orderNumber = orderNumber;
    }

    public String getStatus_pickupAddress() {
        return status_pickupAddress;
    }

    public void setStatus_pickupAddress(String status_pickupAddress) {
        this.status_pickupAddress = status_pickupAddress;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    //    public String getIndex() {
//        return index;
//    }
//
//    public void setIndex(String index) {
//        this.index = index;
//    }

    public void setStatus_uid(String status_uid) {
        this.status_uid = status_uid;
    }

    public String getStatus_uid() {
        return status_uid;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setuserId(String userId) {
        this.userId = userId;
    }

    public void setClientAddress(String clientAddress) {
        ClientAddress = clientAddress;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setNumberOfClient(String numberOfClient) {
        this.NumberOfClient = numberOfClient;
    }

    public void setPickupAddress(String pickupAddress) {
        PickupAddress = pickupAddress;
    }

    public void setTotalPrice(String totalPrice) {
        TotalPrice = totalPrice;
    }

    public void setWeight(String weight) {
        Weight = weight;
    }

    public void setNearest_sign_note(String nearest_sign_note) {
        this.nearest_sign_note = nearest_sign_note;
    }

    public void setNotes_of_shipping(String notes_of_shipping) {
        this.notes_of_shipping = notes_of_shipping;
    }

    public void setPhotoOfProduct(String photoOfProduct) {
        this.photoOfProduct = photoOfProduct;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getuserId() {
        return userId;
    }

    public String getClientAddress() {
        return ClientAddress;
    }

    public String getName() {
        return Name;
    }

    public String getNumberOfClient() {
        return NumberOfClient;
    }

    public String getPickupAddress() {
        return PickupAddress;
    }

    public String getTotalPrice() {
        return TotalPrice;
    }

    public String getWeight() {
        return Weight;
    }

    public String getNearest_sign_note() {
        return nearest_sign_note;
    }

    public String getNotes_of_shipping() {
        return notes_of_shipping;
    }

    public String getPhotoOfProduct() {
        return photoOfProduct;
    }

    public String getStatus() {
        return status;
    }
}
