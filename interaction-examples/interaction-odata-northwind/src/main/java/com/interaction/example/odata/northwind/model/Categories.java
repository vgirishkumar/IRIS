package com.interaction.example.odata.northwind.model;

/*
 * #%L
 * interaction-example-odata-northwind
 * %%
 * Copyright (C) 2012 - 2013 Temenos Holdings N.V.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Categories")
public class Categories implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Basic(optional = false)
  @Column(name = "CategoryID")
  private Integer CategoryID;
  @Lob
  @Column(name = "Description")
  private String Description;
  @Basic(optional = false)
  @Column(name = "CategoryName")
  private String CategoryName;
  @Lob
  @Column(name = "Picture")
  private byte[] Picture;
  @OneToMany(mappedBy = "Category", cascade = CascadeType.ALL)
  private Collection<Products> Products;

  public Categories() {
    this(null, null);
  }

  public Categories(Integer categoryID) {
    this(categoryID, null);
  }

  public Categories(Integer categoryID, String categoryName) {
    this.CategoryID = categoryID;
    this.CategoryName = categoryName;
    this.Products = new ArrayList<Products>();
  }

  public Integer getCategoryID() {
    return CategoryID;
  }

  public void setCategoryID(Integer categoryID) {
    this.CategoryID = categoryID;
  }

  public String getCategoryName() {
    return CategoryName;
  }

  public void setCategoryName(String categoryName) {
    this.CategoryName = categoryName;
  }

  public String getDescription() {
    return Description;
  }

  public void setDescription(String description) {
    this.Description = description;
  }

  public byte[] getPicture() {
    return Picture;
  }

  public void setPicture(byte[] picture) {
    this.Picture = picture;
  }

  public Collection<Products> getProducts() {
    return Products;
  }

  public void setProducts(Collection<Products> productsCollection) {
    this.Products = productsCollection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (CategoryID != null
        ? CategoryID.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof Categories)) {
      return false;
    }
    Categories other = (Categories) object;
    if ((this.CategoryID == null && other.CategoryID != null)
        || (this.CategoryID != null && !this.CategoryID
            .equals(other.CategoryID))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "org.odata4j.examples.producer.model.Categories[categoryID="
        + CategoryID + "]";
  }

}
