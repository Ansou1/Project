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


@interface AccountViewController ()

@end

@implementation AccountViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    /*
     Permet d'avoir l'icone menu, a copier coller dans les fichier de class de chaque vu, si jamais je créer une classe par vue
     Si jamais une vue est lié a une class sans ce morceau de code, on ne pourrap lus revenir en arriere.
     */
    _barButton.target = self.revealViewController;
    _barButton.action = @selector(revealToggle:);
    
    [self.view addGestureRecognizer:self.revealViewController.panGestureRecognizer];
    
    self.profileImageView.layer.cornerRadius = self.profileImageView.frame.size.width / 2;
    self.profileImageView.clipsToBounds = YES;
    self.profileImageView.layer.borderWidth = 2.0f;
    self.profileImageView.layer.borderColor = [UIColor blackColor].CGColor;
    
    //check if it is a guest mode
    GuestMode *guest = [[GuestMode alloc] init];
    if ([guest CheckIfTheUserIsAGuest] == true) {
        //NSLog(@"test test test");
        return;
    }
    
    [self refresh];

}

-(void)TakePhoto{
    picker = [[UIImagePickerController alloc]init];
    picker.delegate = self;
    [picker setSourceType:UIImagePickerControllerSourceTypeCamera];
    [self presentViewController:picker animated:YES completion:NULL];
    //[picker release];
}

-(void)ChooseExisting{
    picker2 = [[UIImagePickerController alloc]init];
    picker2.delegate = self;
    [picker2 setSourceType:UIImagePickerControllerSourceTypePhotoLibrary];
    [self presentViewController:picker2 animated:YES completion:NULL];
    //[picker release];
}

-(void) imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info {
    image = [info objectForKey:UIImagePickerControllerOriginalImage];
    [_profileImageView setImage:image];
    
    [self dismissViewControllerAnimated:YES completion:NULL];
    
    //send photo
    
    NSData *imageData = UIImageJPEGRepresentation([_profileImageView image], 90);
    NSString * urlStr =[NSString stringWithFormat:@"http://163.5.84.253/api/users/%@/photo", Id_global];
    NSString *urlString = urlStr;
    // setting up the request object now
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
    [request setURL:[NSURL URLWithString:urlString]];
    [request setHTTPMethod:@"PUT"];
    
    NSString *boundary = [[NSString alloc]init];
    NSString *contentType = [NSString stringWithFormat:@"image/jpeg; boundary=%@",boundary];
    [request addValue:contentType forHTTPHeaderField: @"Content-Type"];
    NSMutableData *body = [NSMutableData data];
    //[body appendData:[[NSString stringWithFormat:@"\r\n--%@\r\n",boundary] dataUsingEncoding:NSUTF8StringEncoding]];
    //[body appendData:[@"Content-Disposition: form-data; name=\"file\"; filename=\"test.png\"rn" dataUsingEncoding:NSUTF8StringEncoding]];
    //[body appendData:[[NSString stringWithFormat:@"Content-Type: application/%@.jpg\r\n\r\n",@"test"] dataUsingEncoding:NSUTF8StringEncoding]];
    [body appendData:[NSData dataWithData:imageData]];
    //[body appendData:[[NSString stringWithFormat:@"\r\n--%@--\r\n",boundary] dataUsingEncoding:NSUTF8StringEncoding]];
    [request setHTTPBody:body];
    
    //Using Synchronous Request. You can also use asynchronous connection and get update in delegates
    NSData *returnData = [NSURLConnection sendSynchronousRequest:request returningResponse:nil error:nil];
    NSString *returnString = [[NSString alloc] initWithData:returnData encoding:NSUTF8StringEncoding];
    //NSLog(@"--------%@",returnString);
}
    
    /*NSURL* localUrl = (NSURL *)[info valueForKey:UIImagePickerControllerReferenceURL];
    NSLog(@"%@", localUrl.path);*/
    /*
    // get the ref url
    NSURL *refURL = [info valueForKey:UIImagePickerControllerReferenceURL];
    
    // define the block to call when we get the asset based on the url (below)
    ALAssetsLibraryAssetForURLResultBlock resultblock = ^(ALAsset *imageAsset)
    {
        ALAssetRepresentation *imageRep = [imageAsset defaultRepresentation];
        //NSLog(@"[imageRep filename] : %@", [imageRep filename]);
        NSLog(@"[imageRep filename] : %@", [imageRep url]);
        ApiMethod *api = [[ApiMethod alloc]init];
        [api getMethodWithString:@"http://163.5.84.253/api/users/%@/photo"];
    };
    
    // get the asset library and fetch the asset based on the ref url (pass in block above)
    ALAssetsLibrary* assetslibrary = [[ALAssetsLibrary alloc] init];
    [assetslibrary assetForURL:refURL resultBlock:resultblock failureBlock:nil];*/


-(void) imagePickerControllerDidCancel:(UIImagePickerController *)picker {
    [self dismissViewControllerAnimated:YES completion:NULL];
}

-(void) refresh{
    ApiMethod *api = [[ApiMethod alloc]init];
    NSDictionary *dict1 = [api getMethodWithId:(Id_global)];
    
    if (code_global != 200)
    {
        [api popup:dict1];
        return;
    }
    
    NSDictionary *dict2;
    dict2 = [dict1 valueForKeyPath:@"personal_data"];
    
    if ([dict2 objectForKey:(@"firstname")] != [NSNull null])
        self.Name.text = [dict2 objectForKey:(@"firstname")];
    
    if ([dict2 objectForKey:(@"lastname")] != [NSNull null])
        self.Surname.text = [dict2 objectForKey:(@"lastname")];
    
    if ([dict2 objectForKey:(@"email")] != [NSNull null])
        self.Email.text = [dict2 objectForKey:(@"email")];
    
    if ([dict2 objectForKey:(@"username")] != [NSNull null])
        self.Pseudo.text = [dict2 objectForKey:@"username"];
    
    if ([dict2 objectForKey:(@"message")] != [NSNull null])
        self.Message.text = [dict2 objectForKey:@"message"];
    
    if ([dict2 objectForKey:(@"photo")] != [NSNull null])
    {
        urlPhoto = [dict2 objectForKey:@"photo"];
    }
    
    if (urlPhoto != NULL)
    {
        _profileImageView.image = [UIImage imageWithData: [NSData dataWithContentsOfURL:[NSURL URLWithString:urlPhoto]]];
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
    //NSLog(@"", picker.);
}


-(IBAction)validez{
    NSString *tmp_name = self.Name.text;
    NSString *tmp_surname = self.Surname.text;
    NSString *tmp_email = self.Email.text;
    NSString *tmp_message = self.Message.text;
    
    NSString * post =[NSString stringWithFormat:@"{\"firstname\":\"%@\",\"lastname\":\"%@\",\"email\":\"%@\",\"message\":\"%@\"}", tmp_name, tmp_surname, tmp_email, tmp_message];
    NSString * post2 =[NSString stringWithFormat:@"http://163.5.84.253/api/users/%@/personal_data", Id_global];
    
    ApiMethod *api = [[ApiMethod alloc]init];
    NSDictionary *dict1 = [api putMethodWithString:post At:post2];

    if (code_global != 200)
    {
        [api popup:dict1];
        return;
    }
    
    [self.scrollView setContentOffset:CGPointMake(0,-50) animated:YES];
    if ([tmp_email rangeOfString:@"@"].location == NSNotFound) {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"Votre adresse mail n'est pas correct." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
        [alert show];
    }
    else{
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"Les modifications ont bien étè prisent en compte." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
        [alert show];
    }
}
/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

-(IBAction)logout{
    //change the profil picture
    UIAlertController * view=   [UIAlertController
                                 alertControllerWithTitle:@"Mon compte"
                                 message:@"Que desirez vous faire ?"
                                 preferredStyle:UIAlertControllerStyleActionSheet];
    
    UIAlertAction* butt1 = [UIAlertAction
                         actionWithTitle:@"Deconnexion"
                         style:UIAlertActionStyleDefault
                         handler:^(UIAlertAction * action)
                         {
                             //Do some thing here
                             ApiMethod *api = [[ApiMethod alloc]init];
                             [api getMethodWithString:@"http://163.5.84.253/api/logout"];
                             [view dismissViewControllerAnimated:YES completion:nil];
                             [self performSegueWithIdentifier:@"backLogin3" sender:self];
                             
                         }];
    UIAlertAction* butt2 = [UIAlertAction
                            actionWithTitle:@"Supprimer mon compte"
                            style:UIAlertActionStyleDestructive
                            handler:^(UIAlertAction * action)
                            {
                                //Do some thing here
                                NSLog(@"detruit !!!");
                                [view dismissViewControllerAnimated:YES completion:nil];
                                
                            }];
    UIAlertAction* butt3 = [UIAlertAction
                            actionWithTitle:@"Changer de mot de passe"
                            style:UIAlertActionStyleDefault
                            handler:^(UIAlertAction * action)
                            {
                                //--------------------------------
                                UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"" message:@"Changer de mot de passe." preferredStyle:UIAlertControllerStyleAlert]; // 7
                                
                                UIAlertAction *defaultAction = [UIAlertAction actionWithTitle:@"Validez" style:UIAlertActionStyleDefault handler:^(UIAlertAction * action) {
                                
                                    UITextField *test1 = alert.textFields[0];
                                    UITextField *test2 = alert.textFields[1];
                                    ApiMethod *api = [[ApiMethod alloc]init];
                                    
                                    NSString * post =[NSString stringWithFormat:@"{\"current_password\":\"%@\",\"new_password\":\"%@\"}", test1.text, test2.text];
                                    NSString * post2 =[NSString stringWithFormat:@"http://163.5.84.253/api/users/%@", Id_global];
                                    NSDictionary *dict1 = [api optionsMethodWithString:post At:post2];
                                    if (code_global != 204)
                                    {
                                        NSLog(@"%@", dict1);
                                        [api popup:dict1];
                                        return;
                                    }
                                    else{
                                        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"Les modifications ont bien étè prisent en compte." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
                                        [alert show];
                                    }
                                }];
                                
                                [alert addAction:defaultAction];
                                
                                
                                [alert addTextFieldWithConfigurationHandler:^(UITextField *textField1) {
                                    textField1.placeholder = @"Ancien mot de passe";
                                    textField1.accessibilityLabel = @"ancien mot de passe";
                                }];
                                [alert addTextFieldWithConfigurationHandler:^(UITextField *textField2) {
                                    textField2.placeholder = @"Nouveau mot de passe";
                                    textField2.accessibilityLabel = @"nouveau mot de passe";
                                }];
                                
                                
                                UIAlertAction* cancel1 = [UIAlertAction
                                                         actionWithTitle:@"Fermer"
                                                         style:UIAlertActionStyleCancel
                                                         handler:^(UIAlertAction * action)
                                                         {
                                                             [view dismissViewControllerAnimated:YES completion:nil];
                                                             
                                                         }];
                                [alert addAction:cancel1];
                                
                                
                                
                                
                                
                                [self presentViewController:alert animated:YES completion:nil];
                                //--------------------------------
                                
                                
                                
                                [view dismissViewControllerAnimated:YES completion:nil];
                                
                            }];
    UIAlertAction* cancel = [UIAlertAction
                             actionWithTitle:@"Fermer"
                             style:UIAlertActionStyleCancel
                             handler:^(UIAlertAction * action)
                             {
                                 [view dismissViewControllerAnimated:YES completion:nil];
                                 
                             }];
    
    
    
    [view addAction:butt1];
    [view addAction:butt3];
    [view addAction:butt2];
    [view addAction:cancel];
    [self presentViewController:view animated:YES completion:nil];
}

@end

