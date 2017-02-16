//
//  AccountTableViewController.m
//  MSW
//
//  Created by simon on 05/02/2016.
//  Copyright (c) 2016 Score Lab. All rights reserved.
//

#import "AccountTableViewController.h"
#import "ApiMethod.h"
#import "GuestMode.h"

@interface AccountTableViewController ()

@end


@implementation AccountTableViewController

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    // Return the number of sections.
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    // Return the number of rows in the section.
    return 5;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    //check if it is a guest mode
    GuestMode *guest = [[GuestMode alloc] init];
    if ([guest CheckIfTheUserIsAGuestId:self] == true) {
        return;
    }
    [self refresh];
}

-(void) refresh{
    ApiMethod *api = [[ApiMethod alloc]init];
    NSString * post2 =[NSString stringWithFormat:@"https://musicsheetwriter.tk/api/users/%@", Id_global];
    NSDictionary *dict1 = [api ApiMethodGetAfnetworkingAt:post2];
    if (code_global != 200){
        [api popup:dict1 id:self];
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
}

-(IBAction)validez{
    NSString *tmp_name = self.Name.text;
    NSString *tmp_surname = self.Surname.text;
    NSString *tmp_email = self.Email.text;
    NSString *tmp_message = self.Message.text;
    
    NSString * post2 =[NSString stringWithFormat:@"https://musicsheetwriter.tk/api/users/%@/personal_data", Id_global];
    
    ApiMethod *api = [[ApiMethod alloc]init];
    NSMutableDictionary *parametersDictionary = [[NSMutableDictionary alloc]initWithCapacity:0];
    [parametersDictionary setValue:tmp_name forKey:@"firstname"];
    [parametersDictionary setValue:tmp_surname forKey:@"lastname"];
    [parametersDictionary setValue:tmp_email forKey:@"email"];
    [parametersDictionary setValue:tmp_message forKey:@"message"];
    NSDictionary *dict1 = [api ApiMethodPutAfnetworkingAt:post2 Data:parametersDictionary];
    if (code_global != 200){
        [api popup:dict1 id:self];
        return;
    }
    if ([tmp_email rangeOfString:@"@"].location == NSNotFound) {
        UIAlertController * view2 = [UIAlertController
                                     alertControllerWithTitle:@""
                                     message:@"Votre adresse mail n'est pas correct."
                                     preferredStyle:UIAlertControllerStyleAlert];
        
        UIAlertAction* butt1 = [UIAlertAction
                                actionWithTitle:@"Fermer"
                                style:UIAlertActionStyleDefault
                                handler:^(UIAlertAction * action)
                                {
                                    [view2 dismissViewControllerAnimated:YES completion:nil];
                                }];
        [view2 addAction:butt1];
        [self presentViewController:view2 animated:YES completion:nil];
    }
    else{
        UIAlertController * view2 = [UIAlertController
                                     alertControllerWithTitle:@""
                                     message:@"Les modifications ont bien étè prisent en compte."
                                     preferredStyle:UIAlertControllerStyleAlert];
        
        UIAlertAction* butt1 = [UIAlertAction
                                actionWithTitle:@"Fermer"
                                style:UIAlertActionStyleDefault
                                handler:^(UIAlertAction * action)
                                {
                                    [view2 dismissViewControllerAnimated:YES completion:nil];
                                }];
        [view2 addAction:butt1];
        [self presentViewController:view2 animated:YES completion:nil];
    }
}

-(IBAction)logout{
    //change the profil picture
    UIAlertController * view= [UIAlertController
                               alertControllerWithTitle:@"Mon compte"
                               message:@"Que desirez vous faire ?"
                               preferredStyle:UIAlertControllerStyleActionSheet];
    
    UIAlertAction* butt1 = [UIAlertAction
                            actionWithTitle:@"Deconnexion"
                            style:UIAlertActionStyleDefault
                            handler:^(UIAlertAction * action)
                            {
                                ApiMethod *api = [[ApiMethod alloc]init];
                                [api ApiMethodGetAfnetworkingAt:@"https://musicsheetwriter.tk/api/logout"];
                                [[NSUserDefaults standardUserDefaults]setBool:YES forKey:@"deconnection"];
                                [view dismissViewControllerAnimated:YES completion:nil];
                                [self performSegueWithIdentifier:@"backLogin3" sender:self];
                                
                            }];
    /*UIAlertAction* butt2 = [UIAlertAction
                            actionWithTitle:@"Supprimer mon compte"
                            style:UIAlertActionStyleDestructive
                            handler:^(UIAlertAction * action)
                            {
                                NSLog(@"detruit !!!");
                                [view dismissViewControllerAnimated:YES completion:nil];
                                
                            }];*/
    UIAlertAction* butt3 = [UIAlertAction
                            actionWithTitle:@"Changer de mot de passe"
                            style:UIAlertActionStyleDefault
                            handler:^(UIAlertAction * action)
                            {
                                //--------------------------------
                                UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"" message:@"Changer de mot de passe." preferredStyle:UIAlertControllerStyleAlert]; // 7
                                
                                UIAlertAction *defaultAction = [UIAlertAction actionWithTitle:@"Validez" style:UIAlertActionStyleDefault handler:^(UIAlertAction * action) {
                                    
                                    ApiMethod *api = [[ApiMethod alloc]init];
                                    NSString * post2 =[NSString stringWithFormat:@"https://musicsheetwriter.tk/api/users/%@/password", Id_global];
                                    NSMutableDictionary *parametersDictionary = [[NSMutableDictionary alloc]initWithCapacity:0];
                                    [parametersDictionary setValue:alert.textFields[0].text forKey:@"current_password"];
                                    [parametersDictionary setValue:alert.textFields[1].text forKey:@"new_password"];
                                    NSDictionary *dict1 = [api ApiMethodPutAfnetworkingAt:post2 Data:parametersDictionary];
                                    
                                    if (code_global != 204){
                                        [api popup:dict1 id:self];
                                        return;
                                    }
                                    else{
                                        UIAlertController * view2 = [UIAlertController
                                                                     alertControllerWithTitle:@""
                                                                     message:@"Les modifications ont bien étè prisent en compte."
                                                                     preferredStyle:UIAlertControllerStyleAlert];
                                        
                                        UIAlertAction* butt1 = [UIAlertAction
                                                                actionWithTitle:@"Fermer"
                                                                style:UIAlertActionStyleDefault
                                                                handler:^(UIAlertAction * action)
                                                                {
                                                                    [view2 dismissViewControllerAnimated:YES completion:nil];
                                                                }];
                                        [view2 addAction:butt1];
                                        [self presentViewController:view2 animated:YES completion:nil];
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
    //apparemment ne doit pas etre implemente dans l'appli
    //[view addAction:butt2];
    [view addAction:cancel];
    [self presentViewController:view animated:YES completion:nil];
}

@end
