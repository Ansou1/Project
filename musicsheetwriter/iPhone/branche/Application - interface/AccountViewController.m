//
//  MonCompteViewController.m
//  MSW2
//
//  Created by simon on 07/09/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import <AssetsLibrary/AssetsLibrary.h>
#import "AccountViewController.h"
#import "SWRevealViewController.h"
#import "AccountTableViewController.h"

@interface AccountViewController ()

@end

@implementation AccountViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    /*
     Permet d'avoir l'icone menu, a copier coller dans les fichier de class de chaque vu, si jamais je créer une classe par vue
     Si jamais une vue est lié a une class sans ce morceau de code, on ne pourrap lus revenir en arriere.
     */
    
    alert_load = [UIAlertController alertControllerWithTitle:nil message:@"Veuillez patienter\n\n" preferredStyle:UIAlertControllerStyleAlert];
    UIActivityIndicatorView *spinner = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
    spinner.center = CGPointMake(130.5, 65.5);
    spinner.color = [UIColor blackColor];
    [spinner startAnimating];
    [alert_load.view addSubview:spinner];
    [self presentViewController:alert_load animated:NO completion:^{
        _barButton.target = self.revealViewController;
        _barButton.action = @selector(revealToggle:);
        
        [self.view addGestureRecognizer:self.revealViewController.panGestureRecognizer];
        
        self.profileImageView.layer.cornerRadius = self.profileImageView.frame.size.width / 2;
        self.profileImageView.clipsToBounds = YES;
        self.profileImageView.layer.borderWidth = 2.0f;
        self.profileImageView.layer.borderColor = [UIColor blackColor].CGColor;
        
        //check if it is a guest mode
        GuestMode *guest = [[GuestMode alloc] init];
        if ([guest CheckIfTheUserIsAGuestId:self] == true) {
            [alert_load dismissViewControllerAnimated:YES completion:nil];
            return;
        }
        [self refresh];
        [alert_load dismissViewControllerAnimated:YES completion:nil];
    }];
}

-(void)TakePhoto{
    picker = [[UIImagePickerController alloc]init];
    picker.delegate = self;
    [picker setSourceType:UIImagePickerControllerSourceTypeCamera];
    [self presentViewController:picker
                       animated:YES
                     completion:NULL];
    //[picker release];
}

-(void)ChooseExisting{
    picker2 = [[UIImagePickerController alloc]init];
    picker2.delegate = self;
    [picker2 setSourceType:UIImagePickerControllerSourceTypePhotoLibrary];
    [self presentViewController:picker2
                       animated:YES
                     completion:NULL];
    //[picker release];
}

-(void) imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info {    
    image = [info objectForKey:UIImagePickerControllerOriginalImage];
    [_profileImageView setImage:image];
    
    [self dismissViewControllerAnimated:YES
                             completion:NULL];
    //send photo
    
    NSData *imageData = UIImageJPEGRepresentation([_profileImageView image], 90);
    NSString * urlStr =[NSString stringWithFormat:@"https://musicsheetwriter.tk/api/users/%@/photo", Id_global];
    NSString *urlString = urlStr;
    // setting up the request object now
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
    [request setURL:[NSURL URLWithString:urlString]];
    [request setHTTPMethod:@"PUT"];
    
    NSString *boundary = [[NSString alloc]init];
    NSString *contentType = [NSString stringWithFormat:@"image/jpeg; boundary=%@",boundary];
    [request addValue:contentType forHTTPHeaderField: @"Content-Type"];
    NSMutableData *body = [NSMutableData data];
    [body appendData:[NSData dataWithData:imageData]];
    [request setHTTPBody:body];
    NSURLSessionConfiguration *sessionConfiguration = [NSURLSessionConfiguration defaultSessionConfiguration];
    sessionConfiguration.HTTPAdditionalHeaders = @{
                                                   @"api-key"       : @"55e76dc4bbae25b066cb",
                                                   @"Accept"        : @"application/json",
                                                   @"Content-Type"  : [NSString stringWithFormat:@"multipart/form-data; boundary=%@", boundary]
                                                   };
    NSURLSession *session = [NSURLSession sessionWithConfiguration:sessionConfiguration delegate:self delegateQueue:nil];
    NSURLSessionDataTask *uploadTask = [session dataTaskWithRequest:request completionHandler:^(NSData *data, NSURLResponse *response, NSError *error) {
        // Process the response
    }];
    [uploadTask resume];
}

-(void) imagePickerControllerDidCancel:(UIImagePickerController *)picker {
    [self dismissViewControllerAnimated:YES completion:NULL];
}

-(void) refresh{
    ApiMethod *api = [[ApiMethod alloc]init];
    NSString *post = [NSString stringWithFormat:@"https://musicsheetwriter.tk/api/users/%@", Id_global];
    NSDictionary *dict1 = [api ApiMethodGetAfnetworkingAt:post];
    if (code_global != 200){
        [api popup:dict1 id:self];
        return;
    }
    NSDictionary *dict2;
    dict2 = [dict1 valueForKeyPath:@"personal_data"];
    if ([dict2 objectForKey:(@"photo")] != [NSNull null]){
        urlPhoto = [dict2 objectForKey:@"photo"];
    }
    if (urlPhoto != NULL){
        _profileImageView.image = [UIImage imageWithData: [NSData dataWithContentsOfURL:[NSURL URLWithString:urlPhoto]]];
    }
    if (code_global != 200){
        [api popup:dict1 id:self];
        return;
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

-(IBAction)actionButton{
    //change the profil picture
    UIAlertController * view2=   [UIAlertController
                                  alertControllerWithTitle:@"Quelle action desirez vous faire ?"
                                  message:@"Que desirez vous faire ?"
                                  preferredStyle:UIAlertControllerStyleActionSheet];
    
    UIAlertAction* butt1 = [UIAlertAction
                            actionWithTitle:@"Prendre une photo"
                            style:UIAlertActionStyleDefault
                            handler:^(UIAlertAction * action)
                            {
                                [self TakePhoto];
                            }];
    UIAlertAction* butt2 = [UIAlertAction
                            actionWithTitle:@"Mes images"
                            style:UIAlertActionStyleDefault
                            handler:^(UIAlertAction * action)
                            {
                                [self ChooseExisting];
                            }];
    UIAlertAction* cancel = [UIAlertAction
                             actionWithTitle:@"Fermer"
                             style:UIAlertActionStyleCancel
                             handler:^(UIAlertAction * action)
                             {
                                 [view2 dismissViewControllerAnimated:YES completion:nil];
                                 
                             }];
    if ([UIImagePickerController isSourceTypeAvailable: UIImagePickerControllerSourceTypeCamera])
        [view2 addAction:butt1];
    [view2 addAction:butt2];
    [view2 addAction:cancel];
    [self presentViewController:view2 animated:YES completion:nil];
}

@end

