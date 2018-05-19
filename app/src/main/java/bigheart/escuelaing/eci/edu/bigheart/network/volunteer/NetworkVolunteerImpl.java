package bigheart.escuelaing.eci.edu.bigheart.network.volunteer;


import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import bigheart.escuelaing.eci.edu.bigheart.model.Volunteer;
import bigheart.escuelaing.eci.edu.bigheart.network.service.NetworkException;
import bigheart.escuelaing.eci.edu.bigheart.network.service.NetworkServiceVolunteer;
import bigheart.escuelaing.eci.edu.bigheart.network.service.RequestCallback;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkVolunteerImpl implements NetworkVolunteer {

    private static final String BASE_URL = "https://bighearth.herokuapp.com/volunteer/";
    private NetworkServiceVolunteer nsv;
    private ExecutorService backgroundExecutor = Executors.newFixedThreadPool( 1 );

    public NetworkVolunteerImpl() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl( BASE_URL ).addConverterFactory( GsonConverterFactory.create() ).build();
        nsv = retrofit.create( NetworkServiceVolunteer.class );
    }

    @Override
    public void createVolunteer(final Volunteer v,final RequestCallback<Volunteer> requestCallback) {

        backgroundExecutor.execute( new Runnable() {
            @Override
            public void run() {
                Call<Volunteer> call = nsv.createVolunteer(v);
                try {
                    Response<Volunteer> execute =call.execute();
                    requestCallback.onSuccess( execute.body() );
                }
                catch ( Exception e ) {
                    requestCallback.onFailed( new NetworkException( null, e ) );
                }
            }
        } );

    }

    @Override
    public void addSecureTokenInterceptor( final String token )
    {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor( new Interceptor()
        {
            @Override
            public okhttp3.Response intercept( Chain chain )
                    throws IOException
            {
                Request original = chain.request();

                Request request = original.newBuilder().header( "Accept", "application/json" ).header( "Authorization",
                        "Bearer "
                                + token ).method(
                        original.method(), original.body() ).build();
                return chain.proceed( request );
            }
        } );
        Retrofit retrofit = new Retrofit.Builder().baseUrl( BASE_URL ).addConverterFactory( GsonConverterFactory.create() ).client(httpClient.build() ).build();
        nsv = retrofit.create( NetworkServiceVolunteer.class );
    }
}