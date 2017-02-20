//
//  SuscribeViewController.m
//  MSW2
//
//  Created by simon on 29/09/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import "SuscribeViewController.h"
#import "SWRevealViewController.h"

@interface SuscribeViewController ()

@end

@implementation SuscribeViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)valideSuscribe {
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:nil
                                                                   message:@"Veuillez patienter\n\n"
                                                            preferredStyle:UIAlertControllerStyleAlert];
    
    UIActivityIndicatorView *spinner = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
    spinner.center = CGPointMake(130.5, 65.5);
    spinner.color = [UIColor blackColor];
    [spinner startAnimating];
    [alert.view addSubview:spinner];
    [self presentViewController:alert animated:NO completion:^{
        
    ApiMethod *api = [[ApiMethod alloc]init];
        NSMutableDictionary *parametersDictionary = [[NSMutableDictionary alloc]initWithCapacity:0];
        [parametersDictionary setValue:_pseudo.text forKey:@"username"];
        [parametersDictionary setValue:_email.text forKey:@"email"];
        [parametersDictionary setValue:_password.text forKey:@"password"];
        NSDictionary *dict1 = [api ApiMethodPostAfnetworkingAt:@"https://musicsheetwriter.tk/api/users" Data:parametersDictionary];
    NSString *post;
    if (dict1 == NULL || code_global != 200)
    {
        [api popup:dict1 id:self];
    }
    else
    {      
        UIAlertController * view2 = [UIAlertController
                                     alertControllerWithTitle:@""
                                     message:post
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
    [alert dismissViewControllerAnimated:YES completion:^{
        [self performSegueWithIdentifier:@"backLogin" sender:self];
    }];
}

@end
