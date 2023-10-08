package com.ajith.pedal_planet.serviceImpl;

import com.ajith.pedal_planet.DTO.FilterRequest;
import com.ajith.pedal_planet.DTO.MonthlySalesDTO;
import com.ajith.pedal_planet.Enums.Payment;
import com.ajith.pedal_planet.Enums.Status;
import com.ajith.pedal_planet.Repository.*;
import com.ajith.pedal_planet.models.*;
import com.ajith.pedal_planet.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private AddressService addressService;

    @Autowired
    private CartService cartService;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CouponService couponService;

    @Autowired
    private PaymentService paymentService;


    @Autowired
    private FilterService filterService;

    @Autowired
    private VariantService variantService;

    @Autowired
    private VariantRepository variantRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartItemService cartItemService;

    public Order saveOrder(String payment, Long address_id, List<CartItem> cartItemList, Customer existingCustomer,
                           HttpSession session) {
        Optional<Address> address = addressService.findById(address_id);
        Long total_OfferPrice = cartService.getTotalOfferPrice(cartItemList);

        Cart cart = existingCustomer.getCart ();

        // Create and save the Order entity
        Order order = new Order();
        if( !(cart.getCoupon () == null) ) {
            order.setCoupon(cart.getCoupon ());
            order.setTotal ( Float.parseFloat ( cart.getTotal_amount_AfterDiscount () ) );
            session.setAttribute ( "total_Price" , Float.parseFloat ( cart.getTotal_amount_AfterDiscount () )  );
            couponService.decreaseCouponStock(cart.getCoupon());
        }else{
            order.setTotal(total_OfferPrice);
            session.setAttribute ( "total_Price" ,total_OfferPrice );
        }
        order.setOrdered_date(LocalDate.now());
        LocalDate modifiedDate = order.getOrdered_date().plus(7, ChronoUnit.DAYS);
        LocalDate shippingDate = order.getOrdered_date().plus(3, ChronoUnit.DAYS);


        order.setShipping_date ( shippingDate );
        order.setExpecting_date(modifiedDate);
        order.setStatus(Status.PENDING);
        order.setCustomer(existingCustomer);
        order.setAddress(address.get());
        order.setPayment(Payment.valueOf(payment));

        List<OrderItem> orderItems = new ArrayList <> ();
        for (CartItem cartItem : cartItemList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setVariant (cartItem.getVariant ());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getVariant ().getPrice());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }





        cart.setCoupon_discount_amount ( null );
        cart.setTotal_amount_AfterDiscount ( null );
        cart.setCoupon ( null );



        cartRepository.save(cart);
        orderRepository.save(order);
        paymentService.savePaymentDeatils ( order );
        customerRepository.save(existingCustomer);
        variantService.decreaseStock ( orderItems );
        order.setOrderItems ( orderItems );
        cartItemService.removeCartItem(cart);

        return order;
    }


    @Override
    public Optional < Order > getCurrentOrderUsingOrderId (Long orderId) {
        return orderRepository.findById (orderId);
    }

    @Override
    public void changeStatusToReturned (Long orderId) {
        Optional<Order> order = orderRepository.findById(orderId);
        order.get().setStatus(Status.RETURNED);
        variantService.decreaseStock ( order.get ().getOrderItems () );
        orderRepository.save(order.get());
    }
    @Override
    public void changeStatusToCancel (Long orderId) {
        Optional<Order>order = orderRepository.findById ( orderId );
        if (order.isPresent()){
            Order existingOrder = order.get();
            existingOrder.setStatus(Status.CANCELLED);
            variantService.decreaseStock ( existingOrder.getOrderItems ());
            orderRepository.save ( existingOrder );
        }
    }

    @Override
    public List < Order > getAllOrdersWithStatusDelivered () {
         Status status = Status.DELIVERED;
        return  orderRepository.findByStatus(status);
    }

    @Override
    public List<Order> getSalesBetweenTheOrderDate (LocalDate startDate, LocalDate endDate) {
       return orderRepository.findOrdersBetweenDates(startDate, endDate).orElse ( null );
    }

    @Override
    public List < Order > getAllOrdersWithStatusDeliveredBetweenTheDate (List < Order > orderList) {
       List<Order> orderListWithStatusDelivered = new ArrayList<Order>();
        for( Order order : orderList ){
            if( order.getStatus() == Status.DELIVERED){
                orderListWithStatusDelivered.add ( order );
            }
        }
        return orderListWithStatusDelivered;
    }

    @Override
    public List < MonthlySalesDTO > getMonthlySalesData ( ) {
        List < Object[] > resultRows =orderRepository.findMonthlySalesData ();
        return mapToMonthlySalesDTO(resultRows);
    }

    @Override
    public Long getTotalNumberOfOrders() {
        return orderRepository.getTotalNumberOfOrders();
    }

    @Override
    public Long getTotalNumberOfRecentOrders() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusWeeks(1);
        return orderRepository.getTotalNumberOfRecentOrders(startDate, endDate);
    }

    @Override
    public Long getTotalNumberOfReturnedAndCanceledOrders() {
        return orderRepository.getTotalNumberOfReturnedAndCanceledOrders();
    }

    @Override
    public float getTotalSalesAmount ( ) {
        return orderRepository.getTotalSalesAmount();
    }


    @Override
    public float getTotalRefundAmount ( ) {
        return orderRepository.getTotalAmountSumForRefundOrders();
    }




    public float calculateProfitForDeliveredOrders() {

        List < Order > deliveredOrders = orderRepository.findByStatus ( Status.DELIVERED );

        float totalProfit = 0;
        for (Order order : deliveredOrders) {
            float totalWholesalePrice = calculateTotalWholesalePrice ( order );
            float totalOrderAmount = order.getTotal ( );
            float profit = totalOrderAmount - totalWholesalePrice;
            totalProfit += profit;
        }

        return totalProfit;
    }

    @Override
    public Map < String, Integer > calculatePaymentMethodPercentages ( ) {

        Map<String, Integer> paymentMethodPercentages = new HashMap<>();
        paymentMethodPercentages.put("WALLET", orderRepository.countByPaymentWallet());
        paymentMethodPercentages.put("ONLINE", orderRepository.countByPaymentONLINE());
        paymentMethodPercentages.put("COD", orderRepository.countByPaymentCOD());
        System.out.println (paymentMethodPercentages +"hello world" );
        return paymentMethodPercentages;
    }

    private float calculateTotalWholesalePrice(Order order) {
        float totalWholesalePrice = 0;
        for (OrderItem orderItem : order.getOrderItems()) {
            float wholesalePrice = orderItem.getVariant().getWholesalePrice();
            totalWholesalePrice += wholesalePrice;
        }
        return totalWholesalePrice;

}







    private List< MonthlySalesDTO> mapToMonthlySalesDTO (List< Object[]> resultRows) {

         List<MonthlySalesDTO> monthlySalesList = new ArrayList<>();
         for (Object[] row : resultRows) {
             int year = (int) row[0];
             int month = (int) row[1];
             double totalSales = (double) row[2];
             MonthlySalesDTO monthlySalesDTO = new MonthlySalesDTO(year, month, totalSales);
             monthlySalesList.add(monthlySalesDTO);
         }
         return monthlySalesList;
    }

    @Override
    public List < Order > getAllOrders ( ) {
        return orderRepository.findAll ();
    }

    @Override
    public List<Order> filterOrders(List<Order> orders, FilterRequest filterRequest) {
        List<Order> filteredOrders = new ArrayList<>();

        for (Order order : orders) {
            LocalDate orderedDate = order.getOrdered_date ();
            LocalDate currentDate = LocalDate.now ();

            long daysDifference = filterService.calculateDaysDifference(orderedDate,currentDate);


            if (filterRequest.getStatusFilters().contains(order.getStatus()) || filterRequest.getStatusFilters().contains(filterService.isWithinDateRange( Arrays.toString ( filterRequest.getTimeFilters () ), daysDifference)))
                    {
                filteredOrders.add(order);
            }
        }

        return filteredOrders;
    }




}
