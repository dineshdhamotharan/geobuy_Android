package apps.codette.forms;

import android.os.Parcel;
import android.os.Parcelable;

public class Shipping implements Parcelable {

    private String id;
    private String mMessage;
    private String mDate;
    private OrderStatus mStatus;

    public Shipping() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Shipping(String mMessage, String mDate, OrderStatus mStatus) {
        this.mMessage = mMessage;
        this.mDate = mDate;
        this.mStatus = mStatus;
    }

    public String getMessage() {
        return mMessage;
    }

    public void semMessage(String message) {
        this.mMessage = message;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public OrderStatus getStatus() {
        return mStatus;
    }

    public void setStatus(OrderStatus mStatus) {
        this.mStatus = mStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mMessage);
        dest.writeString(this.mDate);
        dest.writeInt(this.mStatus == null ? -1 : this.mStatus.ordinal());
    }

    protected Shipping(Parcel in) {
        this.mMessage = in.readString();
        this.mDate = in.readString();
        int tmpMStatus = in.readInt();
        this.mStatus = tmpMStatus == -1 ? null : OrderStatus.values()[tmpMStatus];
    }

    public static final Parcelable.Creator<Shipping> CREATOR = new Parcelable.Creator<Shipping>() {
        @Override
        public Shipping createFromParcel(Parcel source) {
            return new Shipping(source);
        }

        @Override
        public Shipping[] newArray(int size) {
            return new Shipping[size];
        }
    };
}