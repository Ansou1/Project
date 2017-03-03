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

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

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
    NSDictionary *dict1 = [api postMethodForSuscribeWithUsername:self.pseudo.text Firstname:self.prenom.text Lastname:self.nom.text Email:self.email.text Password:self.password.text Photo:@"null"];//change the null by the url
    NSString *post;
    if (dict1 == NULL || code_global != 200)
    {
        [api popup:dict1];
    }
    else
    {
        post = @"Votre compte a été crée. Veuillez validez votre compte sur le mail que vous venez de recevoir.";
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:post delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
        alert.accessibilityLabel = @"fermer inscription";
        [alert show];
    }
    }];
    [alert dismissViewControllerAnimated:YES completion:^{
        [self performSegueWithIdentifier:@"backLogin" sender:self];
    }];
}

@end
