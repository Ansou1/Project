//
//  ProfilTableViewController.m
//  MSW
//
//  Created by simon on 17/02/2016.
//  Copyright (c) 2016 Score Lab. All rights reserved.
//

#import "ProfilTableViewController.h"
#import "ApiMethod.h"
#import "ProfilViewController.h"
#import "ListSheetTableViewController.h"

@interface ProfilTableViewController ()

@end

@implementation ProfilTableViewController

-(void)viewDidAppear:(BOOL)animated{
    ApiMethod *api = [[ApiMethod alloc]init];
    NSString *url1 = [NSString stringWithFormat:@"https://musicsheetwriter.tk/api/users/%@", self.IdProfil];
    NSDictionary *dict1 = [api ApiMethodGetAfnetworkingAt:url1];
    
    if (code_global != 200)
    {
        [api popup:dict1 id:self];
        return;
    }
    
    NSDictionary *dict2;
    dict2 = [dict1 valueForKeyPath:@"personal_data"];
    
    if ([dict2 objectForKey:(@"firstname")] != [NSNull null])
        self.name.text = [dict2 objectForKey:(@"firstname")];
    
    if ([dict2 objectForKey:(@"lastname")] != [NSNull null])
        self.surname.text = [dict2 objectForKey:(@"lastname")];
    
    if ([dict2 objectForKey:(@"email")] != [NSNull null])
        self.email.text = [dict2 objectForKey:(@"email")];
    
    if ([dict2 objectForKey:(@"username")] != [NSNull null])
        self.pseudo.text = [dict2 objectForKey:@"username"];
    
    if ([dict2 objectForKey:(@"message")] != [NSNull null])
        self.Message.text = [dict2 objectForKey:@"message"];
}

- (void)viewDidLoad {
    [super viewDidLoad];
}

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

-(IBAction)ReadSheet:(id)sender{
    [self performSegueWithIdentifier:@"ListSheet" sender:self];
}

-(void) prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender{
    
    if ([[segue identifier] isEqualToString:@"ListSheet"]){
        idProfil_global = self.IdProfil;
    }
}

@end
