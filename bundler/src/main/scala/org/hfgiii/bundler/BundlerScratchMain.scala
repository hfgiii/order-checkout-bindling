package org.hfgiii.bundler

import org.hfgiii.bundler.models._
import org.hfgiii.bundler.processor.BundleProcessor


object BundlerScratchMain {
  def main(args: Array[String]): Unit = {
    val products       = Product.configureProducts
    val bundleRegistry = BundleRegistry.configureRegistry
    val cart =
    Cart(items = List(ProductCartItem(sku=ProductSKU("2-fruit-orange"),quantity=3,price = products(ProductSKU("2-fruit-orange")).price),
                      ProductCartItem(sku=ProductSKU("4-electronics-camera-sony-XLR"),quantity=1,price = products(ProductSKU("4-electronics-camera-sony-XLR")).price),
                      ProductCartItem(sku=ProductSKU("5-electronics-equipment-tripod"),quantity=1,price = products(ProductSKU("5-electronics-equipment-tripod")).price),
                      ProductCartItem(sku=ProductSKU("3-electronics-battery-AAA-6pack"),quantity=5,price = products(ProductSKU("3-electronics-battery-AAA-6pack")).price)),
                      totalPrice = BigDecimal("1.00"))

    val crossCategoryCart =
      Cart(items = List(ProductCartItem(sku=ProductSKU("4-electronics-camera-sony-XLR"),quantity=1,price = products(ProductSKU("4-electronics-camera-sony-XLR")).price),
                        ProductCartItem(sku=ProductSKU("5-electronics-equipment-tripod"),quantity=1,price = products(ProductSKU("5-electronics-equipment-tripod")).price),
                        ProductCartItem(sku=ProductSKU("3-electronics-battery-AAA-6pack"),quantity=5,price = products(ProductSKU("3-electronics-battery-AAA-6pack")).price)),
                        totalPrice = BigDecimal("1.00"))

    val inCategoryCart =
      Cart(items = List(ProductCartItem(sku=ProductSKU("2-fruit-orange"),quantity=3,price = products(ProductSKU("2-fruit-orange")).price),
        ProductCartItem(sku=ProductSKU("3-electronics-battery-AAA-6pack"),quantity=5,price = products(ProductSKU("3-electronics-battery-AAA-6pack")).price)),
        totalPrice = BigDecimal("1.00"))


    //println(s"cart = $cart")
    //println(s"bundleRegistry.getRelevantBundles(cart) = ${bundleRegistry.getRelevantBundles(cart)}")

    println(s"BundleProcessor(crossCategoryCart).bundleItemsInCart(cart) = ${BundleProcessor(bundleRegistry).bundleItemsInCart(cart)}")
  }
}
