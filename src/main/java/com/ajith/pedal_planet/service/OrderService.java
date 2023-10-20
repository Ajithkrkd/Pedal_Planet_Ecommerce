package com.ajith.pedal_planet.service;

import com.ajith.pedal_planet.DTO.FilterRequest;
import com.ajith.pedal_planet.DTO.MonthlySalesDTO;
import com.ajith.pedal_planet.models.CartItem;
import com.ajith.pedal_planet.models.Customer;
import com.ajith.pedal_planet.models.Order;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderService {
    public Order saveOrder(String payment, Long address_id, List<CartItem> cartItemList, Customer existingCustomer , HttpSession session) ;


    Optional < Order > getCurrentOrderUsingOrderId (Long orderId);

    void changeStatusToReturned (Long orderId);

    List< Order> getAllOrders ( );

    List< Order> filterOrders (List< Order> allOrders, FilterRequest filterRequest);

    void changeStatusToCancel (Long orderId);

    List< Order> getAllOrdersWithStatusDelivered ( );
    List<Order> getSalesBetweenTheOrderDate (LocalDate startDate, LocalDate endDate);

    List< Order> getAllOrdersWithStatusDeliveredBetweenTheDate (List< Order> orderList);

    List< MonthlySalesDTO> getMonthlySalesData ( int selectedYear );

    Long getTotalNumberOfOrders ( );

    Long getTotalNumberOfRecentOrders ( );

    Long getTotalNumberOfReturnedAndCanceledOrders ( );

    Optional<Float> getTotalSalesAmount ( );


    Optional<Float> getTotalRefundAmount ( );


   float calculateProfitForDeliveredOrders();

    Map< String, Integer> calculatePaymentMethodPercentages ( );

    Page< Order> getAllProductWithPagination (int pageNumber, int pageSize);

    Page< Order> searchOrder (int pageNumber, int size, String keyword);

    Page< Order> filterOrder (int pageNumber, int size, String status);

    Optional< Order> findById (long orderId);


    void save (Order existingOrder);

    float findTotalSalesAmount (List< Order> orderList);

    List< Order> getAllOrdersByCustomer ( Customer customer );
}
