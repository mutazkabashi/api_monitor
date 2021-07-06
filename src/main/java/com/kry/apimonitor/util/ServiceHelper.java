package com.kry.apimonitor.util;

import com.kry.apimonitor.domain.User;
import io.vertx.ext.web.api.OperationRequest;

/**
 * A utilty class which provides common functionality to Services layes classes
 * @author Mutaz Hussein Kabashi
 * @version 1.0
 */
public class ServiceHelper {

    //util methods
    public static User extractUserDataFromJwtToken(OperationRequest context){
        User user = new User();
        //userdata in token conatins email-userid we need to get used id only
        System.out.println("user data "+context.getUser());
        String userDataFromToken = context.getUser().getString("sub");
        Integer UserIdFromToken = Integer.valueOf(userDataFromToken.substring(userDataFromToken.lastIndexOf("-")+1));
        user.setId(UserIdFromToken);
        return user;

    }

    public static  User extractPlainUserDataFromHeader(OperationRequest context){
        User user = new User();
        String token = context.getHeaders().get("Authorization");
        System.out.println("token "+token);
        user.setId(Integer.valueOf(token.substring(token.lastIndexOf("-")+1)));
        return user;

    }
}
