package kata;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;


/*
##########################################################################################
  No need to read this class, it is not part of this exercise!!!!!!!!!!!!!!!!! (yet)
##########################################################################################
 */
@Controller("/delivery")
public class DeliveryController {

  @Inject
  DeliveryService deliveryService;

/*
##########################################################################################
  No need to read this method, it is not part of this exercise!!!!!!!!!!!!!!!!! (yet)
##########################################################################################
 */
  @Consumes({MediaType.APPLICATION_JSON})
  @Post
  public HttpResponse<Void> onDelivery(DeliveryEvent deliveryEvent) {
    // from database, find all deliveries for today that are not delivered yet
    List<Delivery> deliverySchedule = new ArrayList<>();
    deliveryService.on(deliveryEvent, deliverySchedule);
    // update deliveries in database
    return HttpResponse.ok();
  }

}
