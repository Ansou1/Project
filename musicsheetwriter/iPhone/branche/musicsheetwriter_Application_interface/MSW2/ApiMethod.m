//
//  ApiMethod.m
//  MSW2
//
//  Created by simon on 29/09/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import "ApiMethod.h"

@implementation ApiMethod

- (void) popup:(NSDictionary *) dict{
    if ([dict objectForKey:(@"message")] != nil)
    {
        if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"LOG-BADCREDENTIALS")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"Les identifiants de connexion sont incorrectes." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-MISSING")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"Il manque des paramètres dans le corps de la requête. " delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-BADFIELD")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"Le format de certains paramètres sont incorrects. " delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-UNAUTHORIZED")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"Vous n'avez pas les permissions requises pour effectuer cette action. " delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-RESNFOUND")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"URl de la requete incorrect." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-USERNFOUND")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"Votre ID utilisateur est introuvable." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-SCORENOTFOUND")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"L'ID de la partition est introuvable." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-SUBNOTFOUND")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"L'ID de l'abonnement est introuvable." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-FAVNOTFOUND")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"L'ID de la partition favorite est introuvable." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-METHNALLOW")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"L’URI est correct mais la méthode HTTP associée n’est pas autorisée." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-BADJSON")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"JSON incorrect." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-DBSERERR")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"La base de données en repond pas." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-DBSCHERR")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"Une anomalie dans le schéma de la base de données est survenue." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"LOG-ACCNOTACTIVE")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"Le compte n’a pas été validé via le mail de confirmation d’inscription." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"LOG-CLOSEDACC")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"Le compte a été clôturé." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"FOR-EMAILNOTFOUND")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"L’adresse email n’appartient à aucun compte." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"FOR-TOKENNOTFOUND")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"Le jeton de réinitialisation de mot de passe est introuvable." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"REG-EALRUSED")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"Le nom d’utilisateur et/ou l’adresse email appartiennent déjà à un autre utilisateur." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"USR-WRONGPASS")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"Le mot de passe n'est pas valide." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"CLO-USERALRCLO")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"Le compte est deja cloturé." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"UPL-BADCONTENTTYPE")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"header non accepté." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"SUB-BADUSERID")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"L’utilisateur identifié n’existe pas." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"FAV-BADSCOREID")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"La partition identifiée n’existe pas." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"PAS-INVALIDEPASSWORD")])
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"Le mot de passe actuel n'est pas correct." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
        else
        {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"Message d'erreur inconnu." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
            [alert show];
        }
    }
    else
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"Erreur inconnu." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
        [alert show];
    }
}

-(NSDictionary *) getMethodWithId:(NSString *)id_receive{
    
    Reachability *networkReachability = [Reachability reachabilityForInternetConnection];
    NetworkStatus networkStatus = [networkReachability currentReachabilityStatus];
    if (networkStatus == NotReachable) {
        PopUpViewController *pop = [[PopUpViewController alloc] init];
        [pop popup];
    } else {
    NSMutableURLRequest *request1 = [[NSMutableURLRequest alloc] init];
    NSString *url1 = [NSString stringWithFormat:@"http://163.5.84.253/api/users/%@", id_receive];
    
    [request1 setURL:[NSURL URLWithString:url1]];
    [request1 setHTTPMethod:@"GET"];
    NSError *error1;
    NSURLResponse *response1;
    NSError *ResponseError1;
    
    NSData *urlData1=[NSURLConnection sendSynchronousRequest:request1 returningResponse:&response1 error:&error1];
    NSDictionary *dict1 = [NSJSONSerialization JSONObjectWithData:urlData1 options:0 error:&ResponseError1];
    //NSLog(@"dict -- %@",dict1);
        
        NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response1;
        int code = [httpResponse statusCode];
        code_global = code;
    return (dict1);
    }

    return NULL;
}

-(NSDictionary *) postMethodWithIdentifier:(NSString *)identifiant Password:(NSString *)password{
    Reachability *networkReachability = [Reachability reachabilityForInternetConnection];
    NetworkStatus networkStatus = [networkReachability currentReachabilityStatus];
    if (networkStatus == NotReachable) {
        PopUpViewController *pop = [[PopUpViewController alloc] init];
        [pop popup];
    } else {
    //--------- this is the post method -----------
    NSString * post =[NSString stringWithFormat:@"{\"login\":\"%@\",\"password\":\"%@\"}", identifiant,password];
    
    NSData *postdata= [post dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:YES];
    NSString *postLength=[NSString stringWithFormat:@"%lu",(unsigned long)[postdata length]];
    NSMutableURLRequest *request= [[NSMutableURLRequest alloc]init];
    
    NSString *str=@"http://163.5.84.253/api/login";
    [request setURL:[NSURL URLWithString:str]];
    [request setHTTPMethod:@"POST"];
    [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
    [request setHTTPBody:postdata];
    NSError *error;
    NSURLResponse *response;
    NSError *ResponseError;
    
    NSData *urlData=[NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
    NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:urlData options:0 error:&ResponseError];
    NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response;
    int code = [httpResponse statusCode];
    code_global = code;
    return (dict);
    }
    return NULL;
}

-(NSDictionary *) postMethodForSuscribeWithUsername:(NSString *)username Firstname:(NSString *)firstname Lastname:(NSString *)lastname Email:(NSString *)email Password:(NSString *)password Photo:(NSString *)photo{
    Reachability *networkReachability = [Reachability reachabilityForInternetConnection];
    NetworkStatus networkStatus = [networkReachability currentReachabilityStatus];
    if (networkStatus == NotReachable) {
        PopUpViewController *pop = [[PopUpViewController alloc] init];
        [pop popup];
    } else {
    NSString * post =[NSString stringWithFormat:@"{\"username\":\"%@\",\"firstname\":\"%@\",\"lastname\":\"%@\",\"email\":\"%@\",\"password\":\"%@\"}", username,firstname,lastname,email,password];
    
    NSData *postdata= [post dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:YES];
    NSString *postLength=[NSString stringWithFormat:@"%lu",(unsigned long)[postdata length]];
    NSMutableURLRequest *request= [[NSMutableURLRequest alloc]init];
    
    
    NSString *str=@"http://163.5.84.253/api/users";
    [request setURL:[NSURL URLWithString:str]];
    [request setHTTPMethod:@"POST"];
    [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
    [request setHTTPBody:postdata];
    NSError *error;
    NSURLResponse *response;
    NSError *ResponseError;
    
    NSData *urlData=[NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
    NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:urlData options:0 error:&ResponseError];
        NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response;
        int code = [httpResponse statusCode];
            code_global = code;
        return dict;
        }
    return NULL;
}
//-------- the real one post method ---------------------
-(NSDictionary *) postMethodWithString:(NSString *)data At:(NSString *)url_str{
    Reachability *networkReachability = [Reachability reachabilityForInternetConnection];
    NetworkStatus networkStatus = [networkReachability currentReachabilityStatus];
    if (networkStatus == NotReachable) {
        PopUpViewController *pop = [[PopUpViewController alloc] init];
        [pop popup];
    } else {
    //--------- this is the post method -----------
    NSString * post =[NSString stringWithFormat:data];
    
    NSData *postdata= [post dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:YES];
    NSString *postLength=[NSString stringWithFormat:@"%lu",(unsigned long)[postdata length]];
    NSMutableURLRequest *request= [[NSMutableURLRequest alloc]init];
    
    
    NSString *str=url_str;
    [request setURL:[NSURL URLWithString:str]];
    [request setHTTPMethod:@"POST"];
    [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
    [request setHTTPBody:postdata];
    NSError *error;
    NSURLResponse *response;
    NSError *ResponseError;

    NSData *urlData=[NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
    NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:urlData options:0 error:&ResponseError];
        NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response;
        int code = [httpResponse statusCode];
        code_global = code;
    return (dict);
    }
    return NULL;
}

//------- the real one get method -------------------
-(NSDictionary *) getMethodWithString:(NSString *)data{
    Reachability *networkReachability = [Reachability reachabilityForInternetConnection];
    NetworkStatus networkStatus = [networkReachability currentReachabilityStatus];
    if (networkStatus == NotReachable) {
        PopUpViewController *pop = [[PopUpViewController alloc] init];
        [pop popup];
    } else {
    NSMutableURLRequest *request1 = [[NSMutableURLRequest alloc] init];
    NSString *url1 = [NSString stringWithFormat:data];
    
    [request1 setURL:[NSURL URLWithString:url1]];
    [request1 setHTTPMethod:@"GET"];
    NSError *error1;
    NSURLResponse *response1;
    NSError *ResponseError1;
    
    NSData *urlData1=[NSURLConnection sendSynchronousRequest:request1 returningResponse:&response1 error:&error1];
    NSDictionary *dict1 = [NSJSONSerialization JSONObjectWithData:urlData1 options:NSJSONReadingMutableContainers error:&ResponseError1];
 
        NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response1;
        int code = [httpResponse statusCode];
        code_global = code;
    return (dict1);
    }
    return NULL;
}

//-------- the real one put method ---------------------
-(NSDictionary *) putMethodWithString:(NSString *)data At:(NSString *)url_str{
    Reachability *networkReachability = [Reachability reachabilityForInternetConnection];
    NetworkStatus networkStatus = [networkReachability currentReachabilityStatus];
    if (networkStatus == NotReachable) {
        PopUpViewController *pop = [[PopUpViewController alloc] init];
        [pop popup];
    } else {
    //--------- this is the post method -----------
    NSString * post =[NSString stringWithFormat:data];
    
    NSData *postdata= [post dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:YES];
    NSString *postLength=[NSString stringWithFormat:@"%lu",(unsigned long)[postdata length]];
    NSMutableURLRequest *request= [[NSMutableURLRequest alloc]init];
    
    
    NSString *str=url_str;
    [request setURL:[NSURL URLWithString:str]];
    [request setHTTPMethod:@"PUT"];
    [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
    [request setHTTPBody:postdata];
    NSError *error;
    NSURLResponse *response;
    NSError *ResponseError;
    
    NSData *urlData=[NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
    NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:urlData options:0 error:&ResponseError];
        NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response;
        int code = [httpResponse statusCode];
        code_global = code;
    return (dict);
    }
    return NULL;
}

-(NSDictionary *) putMethodWithStringForPhoto:(NSString *)data At:(NSString *)url_str{
    Reachability *networkReachability = [Reachability reachabilityForInternetConnection];
    NetworkStatus networkStatus = [networkReachability currentReachabilityStatus];
    if (networkStatus == NotReachable) {
        PopUpViewController *pop = [[PopUpViewController alloc] init];
        [pop popup];
    } else {
        //--------- this is the post method -----------
        NSString * post =[NSString stringWithFormat:data];
        
        NSData *postdata= [post dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:YES];
        NSString *postLength=[NSString stringWithFormat:@"%lu",(unsigned long)[postdata length]];
        NSMutableURLRequest *request= [[NSMutableURLRequest alloc]init];
        
        
        NSString *str=url_str;
        [request setURL:[NSURL URLWithString:str]];
        [request setHTTPMethod:@"PUT"];
        [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
        [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"image/jpeg"];
        [request setHTTPBody:postdata];
        NSError *error;
        NSURLResponse *response;
        NSError *ResponseError;
        
        NSData *urlData=[NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
        NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:urlData options:0 error:&ResponseError];
        NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response;
        int code = [httpResponse statusCode];
        code_global = code;
        return (dict);
    }
    return NULL;
}

//-------- the real one option method ---------------------
-(NSDictionary *) optionsMethodWithString:(NSString *)data At:(NSString *)url_str{
    
    Reachability *networkReachability = [Reachability reachabilityForInternetConnection];
    NetworkStatus networkStatus = [networkReachability currentReachabilityStatus];
    if (networkStatus == NotReachable) {
        PopUpViewController *pop = [[PopUpViewController alloc] init];
        [pop popup];
    } else {
  
    
    //--------- this is the post method -----------
    NSString * post =[NSString stringWithFormat:data];
    
    NSData *postdata= [post dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:YES];
    NSString *postLength=[NSString stringWithFormat:@"%lu",(unsigned long)[postdata length]];
    NSMutableURLRequest *request= [[NSMutableURLRequest alloc]init];
    
    
    NSString *str=url_str;
    [request setURL:[NSURL URLWithString:str]];
    [request setHTTPMethod:@"OPTIONS"];
    [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
    [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
    [request setHTTPBody:postdata];
    NSError *error;
    NSURLResponse *response;
    NSError *ResponseError;
    
    NSData *urlData=[NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
    NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:urlData options:0 error:&ResponseError];
        NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response;
        int code = [httpResponse statusCode];
        code_global = code;
    return (dict);
    }
    return NULL;
}

//-------- the real one option method ---------------------
-(NSDictionary *) deleteMethodWithString:(NSString *)data{
    
    Reachability *networkReachability = [Reachability reachabilityForInternetConnection];
    NetworkStatus networkStatus = [networkReachability currentReachabilityStatus];
    if (networkStatus == NotReachable) {
        PopUpViewController *pop = [[PopUpViewController alloc] init];
        [pop popup];
    } else {
        NSMutableURLRequest *request1 = [[NSMutableURLRequest alloc] init];
        NSString *url1 = [NSString stringWithFormat:data];
        
        [request1 setURL:[NSURL URLWithString:url1]];
        [request1 setHTTPMethod:@"DELETE"];
        NSError *error1;
        NSURLResponse *response1;
        NSError *ResponseError1;
        
        NSData *urlData1=[NSURLConnection sendSynchronousRequest:request1 returningResponse:&response1 error:&error1];
        NSDictionary *dict1 = [NSJSONSerialization JSONObjectWithData:urlData1 options:NSJSONReadingMutableContainers error:&ResponseError1];
        NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response1;
        int code = [httpResponse statusCode];
        code_global = code;
        return (dict1);
    }
    return NULL;
}

-(NSDictionary *) ApiMethodReturnNSDictionaryForOption:(NSString *)option ForTheURL:(NSString *) url_str WithContent:(NSString *)data{
    
    if (data != NULL)
    {
        NSString * post =[NSString stringWithFormat:data];
        
        NSData *postdata= [post dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:YES];
        NSString *postLength=[NSString stringWithFormat:@"%lu",(unsigned long)[postdata length]];
        NSMutableURLRequest *request= [[NSMutableURLRequest alloc]init];
        
        
        NSString *str_url=url_str;
        NSString *str_option = [option uppercaseString];
        [request setURL:[NSURL URLWithString:str_url]];
        [request setHTTPMethod:str_option];
        [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
        [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
        [request setHTTPBody:postdata];
        NSError *error;
        NSURLResponse *response;
        NSError *ResponseError;
        
        NSData *urlData=[NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
        NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:urlData options:0 error:&ResponseError];
        //NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response;
        return (dict);
    }
    else{
        NSMutableURLRequest *request1 = [[NSMutableURLRequest alloc] init];
        NSString *url1 = [NSString stringWithFormat:data];
        
        [request1 setURL:[NSURL URLWithString:url1]];
        NSString *str_option = [option uppercaseString];
        [request1 setHTTPMethod:str_option];
        NSError *error1;
        NSURLResponse *response1;
        NSError *ResponseError1;
        
        NSData *urlData1=[NSURLConnection sendSynchronousRequest:request1 returningResponse:&response1 error:&error1];
        NSDictionary *dict1 = [NSJSONSerialization JSONObjectWithData:urlData1 options:NSJSONReadingMutableContainers error:&ResponseError1];
        //NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response1;
        return (dict1);
    }
    return NULL;
}

-(NSHTTPURLResponse *) ApiMethodReturnNSHTTPURLResponseForOption:(NSString *)option ForTheURL:(NSString *) url_str WithContent:(NSString *)data{
    
    if (data != NULL)
    {
        NSString * post =[NSString stringWithFormat:data];
        
        NSData *postdata= [post dataUsingEncoding:NSASCIIStringEncoding allowLossyConversion:YES];
        NSString *postLength=[NSString stringWithFormat:@"%lu",(unsigned long)[postdata length]];
        NSMutableURLRequest *request= [[NSMutableURLRequest alloc]init];
        
        
        NSString *str_url=url_str;
        NSString *str_option = [option uppercaseString];
        [request setURL:[NSURL URLWithString:str_url]];
        [request setHTTPMethod:str_option];
        [request setValue:postLength forHTTPHeaderField:@"Content-Length"];
        [request setValue:@"application/x-www-form-urlencoded" forHTTPHeaderField:@"Content-Type"];
        [request setHTTPBody:postdata];
        NSError *error;
        NSURLResponse *response;
        NSError *ResponseError;
        
        NSData *urlData=[NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
        [NSJSONSerialization JSONObjectWithData:urlData options:0 error:&ResponseError];
        NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response;
        return (httpResponse);
    }
    else{
        NSMutableURLRequest *request1 = [[NSMutableURLRequest alloc] init];
        NSString *url1 = [NSString stringWithFormat:data];
        
        [request1 setURL:[NSURL URLWithString:url1]];
        NSString *str_option = [option uppercaseString];
        [request1 setHTTPMethod:str_option];
        NSError *error1;
        NSURLResponse *response1;
        NSError *ResponseError1;
        
        NSData *urlData1=[NSURLConnection sendSynchronousRequest:request1 returningResponse:&response1 error:&error1];
        [NSJSONSerialization JSONObjectWithData:urlData1 options:NSJSONReadingMutableContainers error:&ResponseError1];
        NSHTTPURLResponse* httpResponse = (NSHTTPURLResponse*)response1;
        return (httpResponse);
    }
    return NULL;
}

@end

