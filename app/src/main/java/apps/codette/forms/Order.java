package apps.codette.forms;

import java.util.List;

public class Order implements Cloneable {

    public Order(String orderNo){
        this.orderNo = orderNo;
    }
    private float subtotalamount;
    private String id;
    private String orderNo;
    private List<Product> products;
    private String ordertime;
    private float totalamount;
    private Address address;
    private String deliverytime;
    private String status="O";
    // O - Ordered,A - Approved,D - Delivered,
    // Y - Yet to dispatch, C - Cancelled, R - Returned
    private String paymentmode;
    private String paymentstatus;
    private String userid;
    private String useremail;
    private float rating;
    private String paymentId;
    private String orgId;
    private List<Organization> orgDetails;

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    private List<Shipping> shippings;

    public List<Shipping> getShippings() {
        return shippings;
    }

    public void setShippings(List<Shipping> shippings) {
        this.shippings = shippings;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(String ordertime) {
        this.ordertime = ordertime;
    }

    public float getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(float totalamount) {
        this.totalamount = totalamount;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getDeliverytime() {
        return deliverytime;
    }

    public void setDeliverytime(String deliverytime) {
        this.deliverytime = deliverytime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentmode() {
        return paymentmode;
    }

    public void setPaymentmode(String paymentmode) {
        this.paymentmode = paymentmode;
    }

    public String getPaymentstatus() {
        return paymentstatus;
    }

    public void setPaymentstatus(String paymentstatus) {
        this.paymentstatus = paymentstatus;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public float getSubtotalamount() {
        return subtotalamount;
    }

    public void setSubtotalamount(float subtotalamount) {
        this.subtotalamount = subtotalamount;
    }

    public List<Organization> getOrgDetails() {
        return orgDetails;
    }

    public void setOrgDetails(List<Organization> orgDetails) {
        this.orgDetails = orgDetails;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
