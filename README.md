# Order Checkout Bundling

The pupose of this execise is to calculate total order prices by bundling items in the checkout cart. 

## Bundling Types

This implementation uses two types of bundling:

 (1) __In-Category-Bundling__ : All items bundled are in the same product category, e.g. fruit. The items in the bundle are discounted after their number reaches a certain threshold. For example, the first apple is $1.25. Every apple after the first apple is (1 - 0.25)*1.25 = $0.94.
 
 (2) __Cross-Category-Bundling__: The items bundled come from different product cataegories. For instance, this implementation contains two cross-category bundles: __camera-pack__ and __sandwich-lunch__.  The __camera-pack__ bundle contains a camera, tripod and battery six-pack, which is at a 100% discount - a real bargain. The __sandwich-lunch__ bundle contains a loaf of bread (50% off), cornbeef cold-cuts (35% off) and a jar of spicy mustard (50% off)
 
## SKUs

The implementation identifies a cart item by an SKU (Shelf Keeping Unit), which is specified by the business. A bundle in a cart also has an SKU. For an in-category bundle, the bundle SKU is the same as the SKU for the consituent product. A cross-category bundle SKU is the concatenation of all the product SKUs in the bundle.

To distinguish a simple product cart item (_ProductCartItem_) from a bundle cart item (_BundleCartItem_), the implementation uses ,respectively, a _ProductSKU_ and a _BundleSKU_.

## Bundle Registry 

The implementation stores bundles in the _BundleRegistry_. Each bundle is indexed by a _BundleSKU_. The bundle also has a name, which would allow for distinguishing between bundles with the same _BundleSKU_. This functionality was not explored in the exercise implementation.

Given a cart, the _BundleRegistry_ tries to find candidate bundles suitable for the cart. The is done with its _getRelevantBundles_ method. The _BundleRegistry_ is iniialized/configured with an _application.conf_ file by the _BundleRegistry_ method, _configureRegistry_.


## Bundle Processor

Given a _BundleRegistry_, the _BundleProcessor_ tries to create a new cart with as many bundle items it can with its _bundleItemsInCart_ method. It recursively tries to remove simple product items from the cart and replace them with appropriate bundle items. The resultant cart could consist completely of bundled items, simple product items or mixture of both. Notice: the _BundleProcessor_ does not attempt to minimize the total cost of the cart through bundling.


## Cart Pricing Service

The _CartPricingService_ is a simple public API on top of the _BundleRegistry_ and _BundleProcessor_ that calculates the total cart price from the items in the cart. For the purpose or demonstration, _CartPricingService_ has methods that calculate totals without bundling: _priceCartSimply_ and _priceListOfCartsSimply_. The last method does simple totalling for multiple carts concurrently. The equivalent method for totalling multiple carts with bundling is _priceListOfCartsWithBundling_.  


##  Bundling Tests

The implementation includes a __specs2__ test _BundleSpec_ which tests all the bundling components with calls to  _CartPricingService_ methods.

  
 
 