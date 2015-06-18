package gov.gtas.model.udr;

import gov.gtas.model.BaseEntity;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * KnowledgeBase
 */
@Entity
@Table(name = "knowledge_base", catalog = "gtas")
public class KnowledgeBase extends BaseEntity {

	/**
	 * serial version UID.
	 */
	private static final long serialVersionUID = 5027457099159173590L;
	
//	private long version;
	private byte[] kbBlob;
	private Date creationDt;

	@OneToMany(mappedBy="knowledgeBase", fetch=FetchType.LAZY)
	private List<Rule> rulesInKB;
	
	public KnowledgeBase() {
	}

	public KnowledgeBase(long id, Date creationDt) {
		this.id = id;
		this.creationDt = creationDt;
	}

	public KnowledgeBase(long id, byte[] kbBlob, Date creationDt) {
		this.id = id;
		this.kbBlob = kbBlob;
		this.creationDt = creationDt;
	}

//	@Version
//	@Column(name = "VERSION", length = 256)
//	public long getVersion() {
//		return this.version;
//	}
//
//	public void setVersion(long version) {
//		this.version = version;
//	}

	@Column(name = "KB_BLOB")
	public byte[] getKbBlob() {
		return this.kbBlob;
	}

	public void setKbBlob(byte[] kbBlob) {
		this.kbBlob = kbBlob;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATION_DT", nullable = false, length = 19)
	public Date getCreationDt() {
		return this.creationDt;
	}

	public void setCreationDt(Date creationDt) {
		this.creationDt = creationDt;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
//		hashCodeBuilder.append(version);
		hashCodeBuilder.append(kbBlob);
		hashCodeBuilder.append(creationDt);
		return hashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof KnowledgeBase)) {
			return false;
		}
		KnowledgeBase other = (KnowledgeBase) obj;
		EqualsBuilder equalsBuilder = new EqualsBuilder();
//		equalsBuilder.append(version, other.version);
		equalsBuilder.append(kbBlob, other.kbBlob);
		equalsBuilder.append(creationDt, other.creationDt);
		return equalsBuilder.isEquals();
	}

}