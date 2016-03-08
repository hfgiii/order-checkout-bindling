package org.hfgiii.bundler.processor

import org.hfgiii.bundler.models.{Bundle, Cart, BundleRegistry}

import scala.annotation.tailrec


case class BundleProcessor(registry:BundleRegistry) {
    def bundleItemsInCart(cart:Cart):Cart =
      applyBundles(registry.getRelevantBundles(cart),cart)

    @tailrec
    private def applyBundles(bundles:List[Bundle],cart:Cart):Cart =
      bundles match {
        case  Nil            => cart
        case  bundle :: Nil  => applyBundles(Nil,bundle.bundler(cart))
        case  bundle :: rest => applyBundles(rest,bundle.bundler(cart))
      }



}
