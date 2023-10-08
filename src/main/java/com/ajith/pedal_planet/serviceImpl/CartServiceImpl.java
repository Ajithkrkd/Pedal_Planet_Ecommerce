package com.ajith.pedal_planet.serviceImpl;

import com.ajith.pedal_planet.Repository.CartItemRepository;
import com.ajith.pedal_planet.Repository.CartRepository;
import com.ajith.pedal_planet.Repository.ProductRepository;
import com.ajith.pedal_planet.models.*;
import com.ajith.pedal_planet.service.CartService;
import com.ajith.pedal_planet.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductRepository productRepository;

    //This methode is taking the customer Object as parameter which represent whom you want to get
    //create or retrieve the shopping cart
    // if the user have the cart I can return the cart or iam using the or function it is alternative fallback
    //provided by Optional if the user not have cart i will call this methode it create a cart

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @Override
    public Optional<Cart> getCart(Customer customer) {
        return cartRepository.getCartByCustomer(customer)
                .or(() -> {
                    Cart newCart = new Cart();
                    customer.setCart(newCart);
                    newCart.setCustomer(customer);
                    cartRepository.save(newCart);
                    System.out.println ("CREATE CART IS HERE" );
                    return Optional.of(newCart);
                });
    }

    //Creating a new Cart for customer this function take the customer object  and create new
    //cart the return the cart
    @Override
    public Cart createCart(Customer customer) {
        Cart cart = new Cart();
        cart.setCustomer(customer);
        return cart;
    }





    @Override
    public int increaseQuantity(Long itemId ) {
        Customer customer = customerService.findByUsername(getCurrentUsername()).orElseThrow(()->new RuntimeException("customer not found"));
        Cart cart = cartRepository.getCartByCustomer(customer).orElseGet(()->createCart(customer));
        CartItem item =  cart.getCartItems()
                .stream()
                .filter(cartItem -> cartItem.getVariant().getId().equals(itemId))
                .findFirst()
                .orElse(null);

        if (item != null) {

            int availableStock = item.getVariant().getStock();

            if (item.getQuantity() < availableStock) {
                item.setQuantity(item.getQuantity() + 1);
                cartItemRepository.save(item);
            } else {

                throw new RuntimeException("Not enough stock available for this variant.");
            }
        }
        return item.getQuantity();
    }

    @Override
    public int decreaseQuantity(Long itemId) {

        Customer customer = customerService.findByUsername(getCurrentUsername())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Cart cart = cartRepository.getCartByCustomer(customer)
                .orElseGet(() -> createCart(customer));

        // Find the cart item with the given itemId
        CartItem item = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getVariant().getId().equals(itemId))
                .findFirst()
                .orElse(null);

        if (item == null) {
            throw new RuntimeException("Cart item not found"); // Handle this case appropriately
        }

        // Decrease the quantity
        if (item.getQuantity() == 1) {
            cart.getCartItems().remove(item);
            cartRepository.save(cart);
            cartItemRepository.delete(item);
            return 0; // Indicate that the item has been removed
        } else {
            // Decrease the quantity and update the item
            int newQuantity = item.getQuantity() - 1;
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
            cartRepository.save(cart);
            return newQuantity; // Return the new quantity
        }
    }


    @Override
    public void removeFromCartList(CartItem item) {

        Customer customer = item.getCart().getCustomer();
        Cart cart = cartRepository.getCartByCustomer(customer)
                .orElseGet(() -> createCart(customer));

        boolean removed = cart.getCartItems().removeIf(cartItem -> cartItem.equals(item));

        if (removed) {
            cartItemRepository.delete(item);
            cartRepository.save(cart);
        } else {
            throw new IllegalArgumentException("Item not found in cart");
        }
    }

    @Override
    public Long getTotalPrice(List<CartItem> cart) {
        Long total_price = 0L;
        for(CartItem x : cart){
            float v = x.getQuantity() * x.getVariant().getPrice();
            total_price = (long) (total_price + v);
        }
        System.out.println(total_price);
        return total_price;
    }
    @Override
    public Long getTotalOfferPrice(List<CartItem> cart) {
        Long total_Offer_price = 0L;
        for(CartItem x : cart){
            float v = x.getQuantity() * x.getVariant().getOfferPrice();
            total_Offer_price = (long) (total_Offer_price + v);
        }
        System.out.println(total_Offer_price);
        return total_Offer_price;
    }

    @Override
    public void deleteCart (Cart cart) {

    }


    @Override
    public void removeFromTheCartAfterOrder (CartItem cartItem) {
        cartRepository.delete ( cartItem.getCart ( ) );
    }


    // In this methode I'm trying to add an item into the cart list
    //In this methode I'm first getting the customer 'customer_id in the cart so first iam getting the cart then getting the customer
    // Then Iam trying to get the cart if the cart is not got then I called the alternative methode for creating cart
    //then iam using the stream API for filtering the cart items for checking the cart item is already there or not
    // If the cartItem is present I'm going to increase the quantity of the item
    @Override
    public void addToCartList(CartItem item) {

        Customer customer = item.getCart().getCustomer();
        Cart cart = cartRepository.getCartByCustomer(customer)
                .orElseGet(()->createCart(customer));
        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getVariant().equals(item.getVariant()))
                .findFirst();

        if(existingItem.isPresent()){
            if(existingItem.get().getQuantity()<item.getVariant().getStock()){
                updateCartItem(existingItem.get() ,existingItem.get().getQuantity()+1);
            }

        }
        else{
            item.setQuantity(1);
            cart.getCartItems().add(item);
            cartItemRepository.save(item);
        }
        cartRepository.save(cart);
    }

    public void updateCartItem(CartItem cartItem,  int quantity){
        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }
}
