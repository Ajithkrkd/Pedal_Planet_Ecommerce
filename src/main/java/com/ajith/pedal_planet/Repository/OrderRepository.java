package com.ajith.pedal_planet.Repository;

import com.ajith.pedal_planet.Enums.Status;
import com.ajith.pedal_planet.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository <Order ,Long> {
    List< Order> findByStatus (Status status);




    @Query ("SELECT o FROM orders o WHERE o.ordered_date BETWEEN :startDate AND :endDate")
    Optional<List<Order>> findOrdersBetweenDates(LocalDate startDate, LocalDate endDate);


    @Query("SELECT YEAR(o.ordered_date) AS year, MONTH(o.ordered_date) AS month, SUM(o.total) AS total " +
            "FROM orders o " +
            "WHERE o.status = '4' " +  // Filter by status DELIVERED
            "GROUP BY YEAR(o.ordered_date), MONTH(o.ordered_date)")
    List<Object[]> findMonthlySalesData();


    @Query("SELECT COUNT(o) FROM orders o")
    Long getTotalNumberOfOrders();

    @Query("SELECT COUNT(o) FROM orders o WHERE o.ordered_date >= :startDate AND o.ordered_date <= :endDate")
    Long getTotalNumberOfRecentOrders(LocalDate startDate, LocalDate endDate);

    @Query("SELECT COUNT(o) FROM orders o WHERE o.status IN ('9','8', '6')")
    Long getTotalNumberOfReturnedAndCanceledOrders();


    @Query("SELECT SUM(o.total) FROM orders o WHERE o.status = '4'")
    float getTotalSalesAmount ( );

    @Query("SELECT SUM(o.total) FROM orders o WHERE o.status = '9'")
    float getTotalAmountSumForRefundOrders ( );


    @Query("SELECT COUNT(o) FROM orders o WHERE o.payment IN ('0')")
    Integer countByPaymentCOD ();


    @Query("SELECT COUNT(o) FROM orders o WHERE o.payment IN ('1')")
    Integer countByPaymentONLINE ();

    @Query("SELECT COUNT(o) FROM orders o WHERE o.payment IN ('2')")
    Integer countByPaymentWallet ();

}

