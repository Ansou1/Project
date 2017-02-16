//
//  ApiMethod.m
//  MSW2
//
//  Created by simon on 29/09/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import "ApiMethod.h"

@implementation ApiMethod

- (void) popup:(NSDictionary *) dict id:(id)object{
        NSString *errorMessage;
    if ([dict objectForKey:(@"message")] != nil)
    {
        if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"LOG-BADCREDENTIALS")]){
                errorMessage = @"Les identifiants de connexion sont incorrectes.";
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-MISSING")]){
             errorMessage = @"Il manque des paramètres dans le corps de la requête.";
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-BADFIELD")]){
             errorMessage = @"Le format de certains paramètres sont incorrects.";
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-UNAUTHORIZED")]){
             errorMessage = @"Vous n'avez pas les permissions requises pour effectuer cette action.";
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-RESNFOUND")]){
             errorMessage = @"URL de la requete incorrect.";
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-USERNFOUND")]){
             errorMessage = @"Votre ID utilisateur est introuvable.";
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-SCORENOTFOUND")]){
             errorMessage = @"L'ID de la partition est introuvable.";
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-SUBNOTFOUND")]){
             errorMessage = @"L'ID de l'abonnement est introuvable.";
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-FAVNOTFOUND")]){
             errorMessage = @"L'ID de la partition favorite est introuvable.";
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-METHNALLOW")]){
             errorMessage = @"L’URL est correct mais la méthode HTTPS associée n’est pas autorisée.";
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-BADJSON")]){
             errorMessage = @"JSON incorrect.";
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-DBSERERR")]){
             errorMessage = @"La base de données en repond pas.";
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"GLO-DBSCHERR")]){
             errorMessage = @"Une anomalie dans le schéma de la base de données est survenue.";
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"LOG-ACCNOTACTIVE")]){
             errorMessage = @"Le compte n’a pas été validé via le mail de confirmation d’inscription.";
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"LOG-CLOSEDACC")]){
             errorMessage = @"Le compte a été clôturé.";
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"FOR-EMAILNOTFOUND")]){
             errorMessage = @"L’adresse email n’appartient à aucun compte.";
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"FOR-TOKENNOTFOUND")]){
             errorMessage = @"Le jeton de réinitialisation de mot de passe est introuvable.";
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"REG-EALRUSED")]){
             errorMessage = @"Le nom d’utilisateur et/ou l’adresse email appartiennent déjà à un autre utilisateur.";
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"USR-WRONGPASS")]){
             errorMessage = @"Le mot de passe n'est pas valide.";
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"CLO-USERALRCLO")]){
             errorMessage = @"Le compte est deja cloturé.";
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"UPL-BADCONTENTTYPE")]){
             errorMessage = @"header non accepté.";
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"SUB-BADUSERID")]){
             errorMessage = @"L’utilisateur identifié n’existe pas.";
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"FAV-BADSCOREID")]){
             errorMessage = @"La partition identifiée n’existe pas.";
        }
        else if([[dict objectForKey:(@"shortcode")] isEqualToString:(@"PAS-INVALIDEPASSWORD")]){
             errorMessage = @"Le mot de passe actuel n'est pas correct.";
        }
        else{
             errorMessage = @"Message d'erreur inconnu.";
        }
    }
    else{
        errorMessage = @"Probleme connexion internet.";
    }

    UIAlertController * view2 = [UIAlertController
                                 alertControllerWithTitle:@""
                                 message:errorMessage
                                 preferredStyle:UIAlertControllerStyleAlert];
    
    UIAlertAction* butt1 = [UIAlertAction
                            actionWithTitle:@"Fermer"
                            style:UIAlertActionStyleDefault
                            handler:^(UIAlertAction * action)
                            {
                                [view2 dismissViewControllerAnimated:YES completion:nil];
                            }];
    [view2 addAction:butt1];
    [object presentViewController:view2 animated:YES completion:nil];
}

//AFNETWORKING ------------------------------------------
-(NSDictionary *)ApiMethodPostAfnetworkingAt:(NSString *)url Data:(NSMutableDictionary *)parametersDictionary{
    Reachability *networkReachability = [Reachability reachabilityForInternetConnection];
    NetworkStatus networkStatus = [networkReachability currentReachabilityStatus];
    if (networkStatus == NotReachable) {
        PopUpViewController *pop = [[PopUpViewController alloc] init];
        //[pop popupId:self];
    } else {
    AFHTTPSessionManager *manager = [[AFHTTPSessionManager alloc]initWithSessionConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    __block NSDictionary* response = nil;
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    
    manager.completionQueue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    [manager POST:url parameters:parametersDictionary progress:nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        response = responseObject;
        NSHTTPURLResponse* r = (NSHTTPURLResponse*)task.response;
        code_global = r.statusCode;
        dispatch_semaphore_signal(semaphore);
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        NSLog(@"error: %@", error);
        dispatch_semaphore_signal(semaphore);
    }];
    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
    return response;
    }
    return NULL;
}

-(NSDictionary *)ApiMethodGetAfnetworkingAt:(NSString *)url{
    Reachability *networkReachability = [Reachability reachabilityForInternetConnection];
    NetworkStatus networkStatus = [networkReachability currentReachabilityStatus];
    if (networkStatus == NotReachable) {
        PopUpViewController *pop = [[PopUpViewController alloc] init];
        //[pop popupId:self];
    } else {
    AFHTTPSessionManager *manager = [[AFHTTPSessionManager alloc]initWithSessionConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    __block NSDictionary* response = nil;
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    
    manager.completionQueue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    [manager GET:url parameters:nil progress:nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        response = responseObject;
        NSHTTPURLResponse* r = (NSHTTPURLResponse*)task.response;
        code_global = r.statusCode;
        dispatch_semaphore_signal(semaphore);
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        NSLog(@"error: %@", error);
        dispatch_semaphore_signal(semaphore);
    }];
    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
    return response;
    }
    return NULL;
}

-(NSDictionary *)ApiMethodDeleteAfnetworkingAt:(NSString *)url{
    Reachability *networkReachability = [Reachability reachabilityForInternetConnection];
    NetworkStatus networkStatus = [networkReachability currentReachabilityStatus];
    if (networkStatus == NotReachable) {
        PopUpViewController *pop = [[PopUpViewController alloc] init];
        //[pop popupId:self];
    } else {
    AFHTTPSessionManager *manager = [[AFHTTPSessionManager alloc]initWithSessionConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    __block NSDictionary* response = nil;
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    
    manager.completionQueue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    [manager DELETE:url parameters:nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        response = responseObject;
        NSHTTPURLResponse* r = (NSHTTPURLResponse*)task.response;
        code_global = r.statusCode;
        dispatch_semaphore_signal(semaphore);
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        NSLog(@"error: %@", error);
        dispatch_semaphore_signal(semaphore);
    }];
    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
    return response;
    }
    return NULL;
}

-(NSDictionary *)ApiMethodPutAfnetworkingAt:(NSString *)url Data:(NSMutableDictionary *)parametersDictionary{
    Reachability *networkReachability = [Reachability reachabilityForInternetConnection];
    NetworkStatus networkStatus = [networkReachability currentReachabilityStatus];
    if (networkStatus == NotReachable) {
        PopUpViewController *pop = [[PopUpViewController alloc] init];
        //[pop popupId:self];
    } else {
    AFHTTPSessionManager *manager = [[AFHTTPSessionManager alloc]initWithSessionConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]];
    manager.requestSerializer = [AFJSONRequestSerializer serializer];
    [manager.requestSerializer setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    __block NSDictionary* response = nil;
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    
    manager.completionQueue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0);
    [manager PUT:url parameters:parametersDictionary success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
        response = responseObject;
        NSHTTPURLResponse* r = (NSHTTPURLResponse*)task.response;
        code_global = r.statusCode;
        dispatch_semaphore_signal(semaphore);
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        NSLog(@"error: %@", error);
        dispatch_semaphore_signal(semaphore);
    }];
    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
    return response;
    }
    return NULL;
}

-(NSDictionary *) UploadPhotoData:(NSData *)data url:(NSString *)url param:(NSDictionary *)param path:(NSURL *)path
{
    NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration defaultSessionConfiguration];
    AFURLSessionManager *manager = [[AFURLSessionManager alloc] initWithSessionConfiguration:configuration];
    
    NSURL *URL = [NSURL URLWithString:url];
    NSURLRequest *request = [NSURLRequest requestWithURL:URL];

    NSURL *filePath = path;
    NSURLSessionUploadTask *uploadTask = [manager uploadTaskWithRequest:request fromFile:filePath progress:nil completionHandler:^(NSURLResponse *response, id responseObject, NSError *error) {
        if (error) {
            NSLog(@"Error: %@", error);
        } else {
            NSLog(@"Success: %@", responseObject);
        }
    }];
    [uploadTask resume];
    return NULL;
}

@end

