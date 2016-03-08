package org.hfgiii.bundler.models

import com.typesafe.config._
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.util.Properties

sealed trait ItemType

case object ProductItem extends ItemType
case object BundleItem extends ItemType

sealed trait SKU {
  def sku:String
}

case class ProductSKU(sku:String) extends SKU

case class BundleSKU(productSKUs:List[ProductSKU]) extends SKU {
  val sku:String = productSKUs.map(_.sku).mkString(",")
  def contains(_sku:SKU):Boolean = sku.contains(_sku.sku)

}

case class Product(sku:ProductSKU, price:BigDecimal)

object Product {

  val config = ConfigFactory.load(Properties.envOrElse("BUNDLER", "application"))

  def configureProducts:Map[ProductSKU,Product] =
   config.getObjectList("bundles.products").foldLeft(Map.empty[ProductSKU,Product]) { (m,co) =>
     val productEntry = co.entrySet().asScala.toList.head
     val sku = ProductSKU(productEntry.getKey)
     m + (sku -> Product(sku = sku, price = BigDecimal(productEntry.getValue.unwrapped().toString)))
   }

}

sealed trait CartItem {
    def sku: SKU
    def quantity: Int
    def itemType: ItemType
    def isActive: Boolean
    def price: BigDecimal
}

case class ProductCartItem(sku:ProductSKU, quantity:Int, price:BigDecimal, isActive: Boolean = true) extends CartItem {
    def itemType: ItemType = ProductItem
}

case class BundleCartItem(quantity:Int,productItems:List[ProductCartItem],price:BigDecimal, isActive: Boolean = true) extends CartItem {
  val itemType: ItemType = BundleItem
  val sku:SKU = BundleSKU(productSKUs = productItems.map(_.sku))
}

case class Cart(items:List[CartItem],totalPrice:BigDecimal)
