package database;

import java.util.Date;
import java.util.Hashtable;
import java.util.Objects;

public class Customer implements DatabaseConstants {

	private final int c_id;			// client id 
	private final int c_w_id;		// associated warehouse id
	private final int c_d_id;		// associated district id
	private String c_first;			
	private String c_middle;		
	private String c_last;			
	private String c_street1;		
	private String c_street2;
	private String c_city;
	private String c_state;
	private String c_zip;
	private String c_phone;
	private Date c_since;
	private String c_credit; 		// "GC"=good, "BC"=bad
	private double c_credit_lim;
	private double c_discount;
	private double c_balance;
	private double c_ytd_payment;
	private int c_payment_cnt;
	private int c_delivry_cnt;
	private String c_data;

	private Hashtable<Integer, Order> c_orders;

	public Customer(int c_id, int c_d_id, int c_w_id) {

		this.c_id = c_id;
		this.c_d_id = c_d_id;
		this.c_w_id = c_w_id;
		this.c_orders = new Hashtable<Integer, Order>();

		this.c_first = information[(int) Math.random() * 3];
		this.c_middle = information[(int) Math.random() * 3];
		this.c_last = information[(int) Math.random() * 3];
		this.c_street1 = streets1[(int) Math.random() * 3];
		this.c_street2 = streets2[(int) Math.random() * 3];
		this.c_city = cities[(int) Math.random() * 3];
		this.c_state = states[(int) Math.random() * 3];
		this.c_zip = "9512" + Integer.toString((int) Math.random() * 9);
		this.c_phone = phone_nb[(int) Math.random() * 3];
		this.c_since = new Date();
		this.c_credit = credits[(int) ((int) Math.random() * 1.5)];
		this.c_credit_lim = Math.random() * 250000;
		this.c_discount = Math.random() * 1000;
		this.c_balance = Math.random() * 100000;
		this.c_ytd_payment = Math.random() * 2500;
		this.c_payment_cnt = (int) Math.random() * 1000;
		this.c_delivry_cnt = (int) Math.random() * 1000;
		this.c_data = information[(int) Math.random() * 3];
	}

	@Override
	public int hashCode() {
		return Objects.hash(c_id, c_d_id, c_w_id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Customer)) {
			return false;
		}
		Customer other = (Customer) obj;
		return c_d_id == other.c_d_id && c_id == other.c_id && c_w_id == other.c_w_id;
	}

	public double get_c_discount() {
		return this.c_discount;
	}

	public String get_c_credit() {
		return this.c_credit;
	}

	public String get_c_last() {
		return this.c_last;
	}

}