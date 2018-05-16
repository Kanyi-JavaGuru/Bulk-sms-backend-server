package co.ke.bigfootke.app.jpa.entities;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name="credit")
@Cacheable
@Cache(usage=CacheConcurrencyStrategy.READ_ONLY)
public class Credit {
	@Id 
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;

	@Column(name="credit_amount",  nullable=false)
	private Double amount;

	public Credit() {
	}
	
	public Credit(Credit credit) {
		this.id = credit.id;
		this.amount = credit.amount;
	}
	
	public Long getId() {
		return id;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "Credit [id=" + id + ", amount=" + amount + "]";
	}	
}
