//
//  ForgottentPasswordViewController.m
//  MSW2
//
//  Created by simon on 07/10/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import "ForgottentPasswordViewController.h"

@interface ForgottentPasswordViewController ()

@end

@implementation ForgottentPasswordViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)SendEmail{
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:nil message:@"Veuillez patienter\n\n" preferredStyle:UIAlertControllerStyleAlert];
    UIActivityIndicatorView *spinner = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
    spinner.center = CGPointMake(130.5, 65.5);
    spinner.color = [UIColor blackColor];
    [spinner startAnimating];
    [alert.view addSubview:spinner];
    [self presentViewController:alert animated:NO completion:^{
        //do the computation there
        ApiMethod *api = [[ApiMethod alloc]init];
        NSMutableDictionary *parametersDictionary = [[NSMutableDictionary alloc]initWithCapacity:0];
        [parametersDictionary setValue:self.email.text forKey:@"email"];
        NSDictionary *dict = [api ApiMethodPostAfnetworkingAt:@"https://musicsheetwriter.tk/api/forgotten_password" Data:parametersDictionary];
        if (code_global != 204){
            [api popup:dict id:self];
            return;
        }
    }];
    // when i dismiss the alertcontroller, i call the other view else the view cannot be loaded
    [alert dismissViewControllerAnimated:YES completion:^{
        [self performSegueWithIdentifier:@"backLogin2" sender:self];
    }];
}

@end
