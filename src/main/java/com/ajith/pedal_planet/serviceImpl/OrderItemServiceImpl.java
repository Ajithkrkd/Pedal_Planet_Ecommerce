package com.ajith.pedal_planet.serviceImpl;

import com.ajith.pedal_planet.Repository.OrderItemRepository;
import com.ajith.pedal_planet.Repository.VariantRepository;
import com.ajith.pedal_planet.models.CartItem;
import com.ajith.pedal_planet.models.Order;
import com.ajith.pedal_planet.models.OrderItem;
import com.ajith.pedal_planet.models.Variant;
import com.ajith.pedal_planet.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;


@Service
public class OrderItemServiceImpl implements OrderItemService {

    @Autowired
    private VariantRepository variantRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Transactional
    public void saveOrderItem(List<CartItem> cartItemList, Order order) {
        for (CartItem cartItem : cartItemList) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setPrice(cartItem.getVariant().getOfferPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setVariant(cartItem.getVariant());
            orderItemRepository.save(orderItem);
        }
    }

    @Override
    public void decresethQuantity(Order order) {
        List<OrderItem>orderItems = orderItemRepository.findAllByOrder_Id(order.getId());
        for(OrderItem x : orderItems){
            Variant variant = x.getVariant();
            int existingStock = variant.getStock();
            variant.setStock(existingStock - x.getQuantity());
               variantRepository.save(variant);

        }
    }

    @Override
    public List < OrderItem > findAllByOrder_Id (long orderId) {
        return orderItemRepository.findAllByOrder_Id ( orderId );
    }
}
