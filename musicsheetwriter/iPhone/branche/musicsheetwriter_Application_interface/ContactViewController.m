//
//  ContactViewController.m
//  MSW
//
//  Created by simon on 09/12/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import "SWRevealViewController.h"
#import "ContactViewController.h"
#import "ApiMethod.h"

@interface ContactViewController ()

@end

@implementation ContactViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    _barButton.target = self.revealViewController;
    _barButton.action = @selector(revealToggle:);
    
    [self.view addGestureRecognizer:self.revealViewController.panGestureRecognizer];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)send:(id)sender {
    NSString * post =[NSString stringWithFormat:@"{\"email\":\"%@\",\"subject\":\"%@\",\"message\":\"%@\"}", _email.text, _object.text, _message.text];
    ApiMethod *api = [[ApiMethod alloc]init];
    NSDictionary *dict1 = [api postMethodWithString:post At:@"http://163.5.84.253/api/contact"];
    if (code_global != 201)
    {
        [api popup:dict1];
        return;
    }
    else
    {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"" message:@"Le message a bien été envoyé." delegate:self cancelButtonTitle:@"Fermer" otherButtonTitles:nil];
        [alert show];
    }

}
@end
