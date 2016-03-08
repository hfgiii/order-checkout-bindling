package org.hfgiii.bundler.models

import com.typesafe.config._
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.util.Properties

sealed trait BundleType {
  def name:String
}
case object CrossCategory  extends BundleType {
    def name:String = "cross-category"
}
case object InCategory     extends BundleType  {
  def name:String = "in-category"
}

sealed trait Bundle {
  def name:String
  def key:BundleSKU
  def bundler(cart:Cart):Cart
}

case class SkuDiscount(sku:SKU, discount: BigDecimal)

case class InCategoryBundle(name:String,key:BundleSKU, threshold: Int, discount: SkuDiscount) extends Bundle {

  def bundler(cart:Cart):Cart =
    cart.items.find(item => item.itemType == ProductItem && item.sku.sku == key.sku).fold(cart) { item =>

       val restItems   = cart.items diff List(item)
       val bundlePrice = (threshold  + (item.quantity - threshold)*(1 - discount.discount)) * item.price

       Cart(items = BundleCartItem(quantity = 1,List(item.asInstanceOf[ProductCartItem]), price = bundlePrice) :: restItems , totalPrice = BigDecimal(0.0) )
    }
}

case class CrossCategoryBundle(name:String,key:BundleSKU, discounts: List[SkuDiscount]) extends Bundle {

  def bundler(cart:Cart):Cart = {
    val items =
      key.productSKUs.foldLeft(List.empty[CartItem]) { (l, sku) =>
        cart.items.find(item => item.itemType == ProductItem && item.sku.sku == sku.sku).fold(l) { item =>
          item :: l
        }
      }

    val restItems = cart.items diff items

    val bundleCartItem =
    items.foldLeft(BundleCartItem(quantity = 1,productItems = List.empty[ProductCartItem], price = BigDecimal(0d))) { (bci, item) =>
      discounts.find(_.sku.sku == item.sku.sku).fold(bci) { discount =>
        bci.copy(price = bci.price + (1 - discount.discount) * item.price, productItems = item.asInstanceOf[ProductCartItem].copy(quantity = 1) :: bci.productItems)
      }
    }

     val leftOverItems = items.foldLeft(List.empty[ProductCartItem]) { (l,ci) =>
       if(ci.quantity - 1 == 0) l
       else
         ProductCartItem(sku = ci.sku.asInstanceOf[ProductSKU],quantity = ci.quantity - 1, price = ci.price, isActive = ci.isActive) :: l

     }
     Cart(items = bundleCartItem :: (restItems ++ leftOverItems), totalPrice = BigDecimal(0.0))
  }
}

case class BundleRegistry(bundleMap:Map[BundleSKU,Bundle]) {

   private val keys = bundleMap.keys.toList

   def getRelevantBundles(cart:Cart):List[Bundle] =
    cart.items.map(_.sku).foldLeft(List.empty[Bundle]) { (l,sku) =>
      l ++ keys.filter(_.contains(sku)).map( b => bundleMap(b))
    }.distinct
}


object BundleRegistry {

  val config = ConfigFactory.load(Properties.envOrElse("BUNDLER", "application"))

  private def genInCategoryBundle(configPath: String, name: String): InCategoryBundle = {
    val bundleSku = BundleSKU(productSKUs = config.getStringList(s"$configPath.skus").map(sku => ProductSKU(sku)).toList)
    InCategoryBundle(
      name = name,
      key  = bundleSku,
      threshold = config.getInt(s"$configPath.threshold"),
      discount =  SkuDiscount(sku = bundleSku.productSKUs.head, discount = BigDecimal(config.getString(s"$configPath.discount")))
    )
}

  private def genCrossCategoryBundle(configPath:String, name:String):CrossCategoryBundle = {
      val bundleSku    =
           BundleSKU(productSKUs = config.getStringList(s"$configPath.skus").map(sku => ProductSKU(sku)).toList)

      val skuDiscounts =
          config.getObjectList(s"$configPath.discounts").map { co =>
              val skuDiscountEntry = co.entrySet().asScala.toList.head
              SkuDiscount(sku = ProductSKU(skuDiscountEntry.getKey), discount = BigDecimal(skuDiscountEntry.getValue.unwrapped().toString))
          }
      CrossCategoryBundle(name = name, key = bundleSku, discounts = skuDiscounts.toList)
  }

  def configureRegistry:BundleRegistry = {

    val bundleNames = config.getStringList("bundles.names")
    val bundleMap =
    bundleNames.foldLeft(Map.empty[BundleSKU,Bundle]) { (m, name) =>
      val path = s"bundles.$name"
      config.getString(s"$path.type") match {
        case "in-category" =>  {
          val bundle = genInCategoryBundle(path, name)
          m + (bundle.key -> bundle)
        }
        case "cross-category" =>  {
          val bundle = genCrossCategoryBundle(path, name)
          m + (bundle.key -> bundle)
        }
        case _ => m
      }
    }

    BundleRegistry(bundleMap = bundleMap)
  }
}