package bigheart.escuelaing.eci.edu.bigheart.network.service;

import java.util.List;

import bigheart.escuelaing.eci.edu.bigheart.model.Event;
import bigheart.escuelaing.eci.edu.bigheart.network.login.LoginWrapper;
import bigheart.escuelaing.eci.edu.bigheart.network.login.Token;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import okhttp3.MultipartBody;
import retrofit2.http.Part;
import retrofit2.http.Multipart;


public interface NetworkServiceEvent {

    @POST( "event/createEvent/{NIT}" )
    Call<Event> createEvent(@Body Event e,@Path("NIT") int NIT);

    @POST( "event/unrol/{eventId}/{email}" )
    Call<Boolean> unrol(@Path("eventId") String eventId,@Path("email") String email);

    @POST( "event/volunteerInEvent/{eventId}/{email}" )
    Call<Boolean> volunteerInEvent(@Path("eventId") String eventId,@Path("email") String email);

    @POST( "event/rol/{eventId}/{email}" )
    Call<Boolean> rol(@Path("eventId") String eventId,@Path("email") String email);

    @GET(" event/AllEvent ")
    Call<List<Event>> getAllEvents();
    
    @GET("event/{idEvent}")
    Call<Event> getEventById(@Path("idEvent") String idEvent);

    @Multipart
    @POST( "event/{eventId}/image/upload" )
    Call<Event> setEventImage(@Path("eventId") String eventId,@Part MultipartBody.Part m);

}