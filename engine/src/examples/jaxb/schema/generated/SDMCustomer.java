//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.09.02 at 11:23:17 AM IDT 
//

package examples.jaxb.schema.generated;

import javax.xml.bind.annotation.*;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element ref="{}name"/>
 *         &lt;element ref="{}location"/>
 *       &lt;/all>
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "SDM-customer")
public class SDMCustomer {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected Location location;
    @XmlAttribute(name = "id", required = true)
    protected int id;

    /**
     * Gets the value of the name property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getName () {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *        allowed object is {@link String }
     * 
     */
    public void setName (String value) {
        this.name = value;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return possible object is {@link Location }
     * 
     */
    public Location getLocation () {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *        allowed object is {@link Location }
     * 
     */
    public void setLocation (Location value) {
        this.location = value;
    }

    /**
     * Gets the value of the id property.
     * 
     */
    public int getId () {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId (int value) {
        this.id = value;
    }

}
