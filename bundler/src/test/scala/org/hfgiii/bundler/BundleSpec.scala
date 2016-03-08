package org.hfgiii.bundler

import org.hfgiii.bundler.models._
import org.hfgiii.bundler.service.CartPricingService
import org.specs2.mutable.Specification

class BundleSpec extends Specification {
  val service  = new CartPricingService(BundleRegistry.configureRegistry)
  val products = Product.configureProducts
  val appleSKU    = ProductSKU("1-fruit-apple")
  val orangeSKU   = ProductSKU("2-fruit-orange")
  val batterySKU  = ProductSKU("3-electronics-battery-AAA-6pack")
  val cameraSKU   = ProductSKU("4-electronics-camera-sony-XLR")
  val tripodSKU   = ProductSKU("5-electronics-equipment-tripod")
  val breadSKU    = ProductSKU("6-bakery-bread-rye")
  val cornBeefSKU = ProductSKU("7-coldcuts-cornbeef")
  val mustardSKU  = ProductSKU("8-condiments-mustard-spicy")


  val fruitAndCameraCart =
    Cart(items = List(ProductCartItem(sku=orangeSKU,quantity=3,price = products(orangeSKU).price),
      ProductCartItem(sku=cameraSKU,quantity=1,price = products(cameraSKU).price),
      ProductCartItem(sku=tripodSKU,quantity=1,price = products(tripodSKU).price),
      ProductCartItem(sku=batterySKU,quantity=5,price = products(batterySKU).price)),
      totalPrice = BigDecimal(0d))

  val cameraCart =
    Cart(items = List(ProductCartItem(sku=cameraSKU,quantity=1,price = products(cameraSKU).price),
      ProductCartItem(sku=tripodSKU,quantity=1,price = products(tripodSKU).price),
      ProductCartItem(sku=batterySKU,quantity=1,price = products(batterySKU).price)),
      totalPrice = BigDecimal(0d))

  val inCategoryCart =
    Cart(items = List(ProductCartItem(sku=orangeSKU,quantity=3,price = products(orangeSKU).price),
      ProductCartItem(sku=appleSKU,quantity=5,price = products(appleSKU).price),
      ProductCartItem(sku=batterySKU,quantity=5,price = products(batterySKU).price)),
      totalPrice = BigDecimal(0d))

  val bigLunchCart =
    Cart(items = List(ProductCartItem(sku=orangeSKU,quantity=3,price = products(orangeSKU).price),
      ProductCartItem(sku=orangeSKU,quantity=3,price = products(orangeSKU).price),
      ProductCartItem(sku=appleSKU,quantity=1,price = products(appleSKU).price),
      ProductCartItem(sku=mustardSKU,quantity=1,price = products(mustardSKU).price),
      ProductCartItem(sku=cornBeefSKU,quantity=2,price = products(cornBeefSKU).price)),
      totalPrice = BigDecimal(0d))

  "All total cart prices should be cheaper when bundled" should {
    " for fruitAndCameraCart" in {
      service.priceCartWithBundling(fruitAndCameraCart).totalPrice must be_<(service.priceCartSimply(fruitAndCameraCart).totalPrice)
    }
    " for cameraCart" in {
      service.priceCartWithBundling(cameraCart).totalPrice must be_<(service.priceCartSimply(cameraCart).totalPrice)
    }
    " for inCategoryCart" in {
      service.priceCartWithBundling(inCategoryCart).totalPrice must be_<(service.priceCartSimply(inCategoryCart).totalPrice)
    }
    " for bigLunchCart" in {
      service.priceCartWithBundling(bigLunchCart).totalPrice must be_<(service.priceCartSimply(bigLunchCart).totalPrice)
    }
  }

  "Multiple carts can be priced concurrently" should {
    " with simply pricing" in {
      service.priceListOfCartsSimply(List(fruitAndCameraCart,cameraCart,inCategoryCart,bigLunchCart)).map { results =>
          results.zip(List(BigDecimal("113.65"),BigDecimal("77.90"),BigDecimal("50.00"),BigDecimal("21.15"))).foldLeft(results.length === 4) { (matcher,tpl) =>
            val (cart,result) = tpl
                matcher and cart.totalPrice === result
          } and true === true
      }.await

    }

    " with bundled pricing" in {
      service.priceListOfCartsWithBundling(List(fruitAndCameraCart,cameraCart,inCategoryCart,bigLunchCart)).map { results =>
        results.zip(List(BigDecimal("75.3100"),BigDecimal("45.4350"),BigDecimal("42.1250"),BigDecimal("16.2925"))).foldLeft(results.length === 4) { (matcher,tpl) =>
          val (cart,result) = tpl
          matcher and cart.totalPrice === result
        } and true === true
      }.await
    }
  }
}
