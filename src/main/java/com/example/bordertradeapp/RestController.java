package com.example.bordertradeapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController()
@RequestMapping("api/1.1")
public class RestController // Rest API includes all the URLs under the RestController
{
    @Autowired
    TradeMessageRepository tradeMessageRepository;

    @Autowired
    ProductofferRepository productofferRepository;

    @Autowired
    UserdetailRepository userdetailRepository;

   // @Autowired
   // OrderRepository orderRepository;

    @PostMapping("updateuserdetails")
    Message updateuserdetails(@RequestParam("fname") String fname, @RequestParam("lname") String lname, @RequestParam("phone") String phone, @RequestParam("type") String type, HttpSession session) throws IOException {

        User user = new User();
        user.setUsername((String)(session.getAttribute("username")));
        user.setFname(fname);
        user.setLname(lname);
        user.setPhone(phone);
        user.setType(type);

        ObjectMapper objectMapper = new ObjectMapper();


        //Jsonb jsonb = JsonbBuilder.create();
        //String userJson = jsonb.toJson(user);

        BufferedReader reader = Files.newBufferedReader(Paths.get("D:\\SOftware Dev and Training Material\\NewWinCode\\tcsdec22\\cbt\\src\\main\\resources\\data\\userdetails.txt"));
        String line;

        Map<String,User> userDetailsMap = new HashMap<>();

        while ((line = reader.readLine())!=null)
        {
            User tempUser = objectMapper.readValue(line,User.class);
            userDetailsMap.put(tempUser.getUsername(),tempUser);
        }
        userDetailsMap.put(user.getUsername(),user);

        BufferedWriter writer = Files.newBufferedWriter(Paths.get("D:\\SOftware Dev and Training Material\\NewWinCode\\tcsdec22\\cbt\\src\\main\\resources\\data\\userdetails.txt"));

        for (Map.Entry<String,User> entry: userDetailsMap.entrySet() )
        {
            writer.append(objectMapper.writeValueAsString(entry.getValue())+"\n");
        }

        writer.flush();

        Message message = new Message();
        message.setMessage("User Details Updated");

        return message;

    }

    @PostMapping("saveOffer")
    Message saveOffer(@RequestParam("hscode") String hscode, @RequestParam("offername") String offername, @RequestParam("unit") String unit, @RequestParam("unitprice") Integer unitprice, @RequestParam("qty") Integer qty, HttpSession session ) throws IOException {



        String offerid = String.valueOf((int) (Math.random()*10000));

        Productoffer offer = new Productoffer();
        offer.setId(offerid);
        offer.setUsername((String)session.getAttribute("username"));
        offer.setHscode(hscode);
        offer.setOffername(offername);
        offer.setUnit(unit);
        offer.setUnitprice(Float.valueOf(unitprice));
        offer.setQty(qty);

        BufferedReader bufferedReader = Files.newBufferedReader(Paths.get("D:\\SOftware Dev and Training Material\\NewWinCode\\tcsdec22\\cbt\\src\\main\\resources\\data\\offers.txt"));
        String line;

        List<Productoffer> offersList = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();

        while ((line = bufferedReader.readLine())!=null)
        {
            Productoffer tempOffer = objectMapper.readValue(line,Productoffer.class);
            offersList.add(tempOffer);
        }
        offersList.add(offer);

        BufferedWriter writer = Files.newBufferedWriter(Paths.get("D:\\SOftware Dev and Training Material\\NewWinCode\\tcsdec22\\cbt\\src\\main\\resources\\data\\offers.txt"));

        for (Productoffer tempOffer: offersList )
        {
            writer.append(objectMapper.writeValueAsString(tempOffer)+"\n");
        }

        writer.flush();

        Message message = new Message();
        message.setMessage("Offer Saved Successfully");
        return message;
    }

    @PostMapping("saveOfferDb")
    Message saveOfferDb(@RequestParam("hscode") String hscode, @RequestParam("offername") String offername, @RequestParam("unit") String unit, @RequestParam("unitprice") String unitprice, @RequestParam("qty") Integer qty, HttpSession session ) throws IOException, SQLException {
        String offerid = String.valueOf((int) (Math.random()*10000));

        Productoffer offer = new Productoffer();
        offer.setId(offerid);
        offer.setUsername((String)session.getAttribute("username"));
        offer.setHscode(hscode);
        offer.setOffername(offername);
        offer.setUnit(unit);
        offer.setUnitprice(Float.valueOf(unitprice));
        offer.setQty(qty);


       ProductOfferDAO productOfferDAO = new ProductOfferDAO();
       productOfferDAO.save(offer);

       Message message = new Message();
       message.setMessage("Offer Saved Successfully in DataBase");
       return message;
    }

    @PostMapping("createorder")
    public Message createOrder(@RequestParam("offerid") String offerId, HttpSession session) throws SQLException {
        String orderid = String.valueOf((int) (Math.random()*10000));

        Order order = new Order();
        order.setId(orderid);
        order.setOfferid(offerId);
        order.setUsername((String) session.getAttribute("username"));

        OrderDAO orderDAO = new OrderDAO();
        orderDAO.create(order);

        Message message = new Message();
        message.setMessage("Order was successfully placed");
        return message;
    }

    @GetMapping("fetchOrdersBuyerwise")
    public List<OrderView> fetchAllOrdersBuyerwise(HttpSession session) throws SQLException
    {
        OrderViewDAO orderViewDAO = new OrderViewDAO();
        return orderViewDAO.fetchAllOrdersBuyerwise((String)session.getAttribute("username"));
    }

    @GetMapping("testFetchOrdersBuyerwise")
    public List<OrderView> testFetchAllOrdersBuyerwise(@RequestParam("buyername") String buyername) throws SQLException
    {
        OrderViewDAO orderViewDAO = new OrderViewDAO();
        return orderViewDAO.fetchAllOrdersBuyerwise(buyername);
    }

    @PostMapping("message") // message | GET - fetch all messages of this user | POST - post a new message | DEL - Delete all messages for this User
    public Message saveMessage(@RequestParam("message") String message, @RequestParam("offerid") String offerid, HttpSession session)
    {
        String orderid = String.valueOf((int) (Math.random()*10000));

        Trademessage tradeMessage = new Trademessage();
        tradeMessage.setSender(userdetailRepository.findById((String) session.getAttribute("username")).get());
        tradeMessage.setReceiver(userdetailRepository.findById(productofferRepository.findById(offerid).get().getUsername()).get());
        tradeMessage.setMessage(message);
        //tradeMessage.setTimestamp(new LocalDateTime());
        tradeMessage.setId(orderid);
        tradeMessageRepository.save(tradeMessage);

       // orderRepository.fetchSellerInfo();

        Message message1 = new Message();
        message1.setMessage("The Message was sent successfully");
        return message1;
    }

    @GetMapping("message")
    public Message getAllMessages()
    {
        return new Message();
    }

    @GetMapping("offer/{hscode}")
    public List<Productoffer> getAllOffers(@PathVariable("hscode") String hscode) throws SQLException
    {
        ProductOfferDAO productOfferDAO = new ProductOfferDAO();
        return productOfferDAO.fetchAllOffersByHscode(hscode);
    }

    @GetMapping("order/{hscode}")
    public List<OrderView> getAllOrders(@PathVariable("hscode") String hscode) throws SQLException
    {
        OrderViewDAO orderViewDAO = new OrderViewDAO();
        return orderViewDAO.fetchAllOrdersByHscode(hscode);
    }

}
