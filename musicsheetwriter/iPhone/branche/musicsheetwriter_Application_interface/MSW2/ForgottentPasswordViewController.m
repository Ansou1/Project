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

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

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
        NSString * post =[NSString stringWithFormat:@"{\"email\":\"%@\"}", self.email.text];
        NSDictionary *dict = [api postMethodWithString:post At:@"http://163.5.84.253/api/forgotten_password"];
        //NSLog(@"//*// %@", dict);
        if (code_global != 200)
        {
            [api popup:dict];
            return;
        }
    }];
    // when i dismiss the alertcontroller, i call the other view else the view cannot be loaded
    [alert dismissViewControllerAnimated:YES completion:^{
        [self performSegueWithIdentifier:@"backLogin2" sender:self];
    }];
}

@end
