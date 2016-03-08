package org.hfgiii.bundler.service

import org.hfgiii.bundler.models._
import org.hfgiii.bundler.processor.BundleProcessor
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class CartPricingService(bundleRegistry:BundleRegistry) {
   private val processor = BundleProcessor(bundleRegistry)

   def priceCartSimply(cart:Cart):Cart = {
    val p =
      cart.items.foldLeft(BigDecimal(0d)) { (price,item) =>
        price + item.price}

    cart.copy(totalPrice =
    cart.items.foldLeft(BigDecimal(0d)) { (price,item) =>
      price + item.price * item.quantity
    })
    }

   def priceCartWithBundling(cart:Cart):Cart =
     priceCartSimply(processor.bundleItemsInCart(cart))

   def priceListOfCartsSimply(carts:List[Cart]):Future[List[Cart]] =
   Future.sequence {
     carts.map { cart =>
       Future(priceCartSimply(cart))
     }
   }

  def priceListOfCartsWithBundling(carts:List[Cart]):Future[List[Cart]] =
    Future.sequence {
      carts.map { cart =>
        Future(priceCartWithBundling(cart))
      }
    }
}
